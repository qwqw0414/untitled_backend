package com.joje.untitled.service.impl;

import com.joje.untitled.common.constants.RoleType;
import com.joje.untitled.common.security.JwtTokenProvider;
import com.joje.untitled.exception.ExpiredRefreshTokenException;
import com.joje.untitled.exception.ExpiredTokenException;
import com.joje.untitled.exception.InvalidTokenException;
import com.joje.untitled.exception.SignonException;
import com.joje.untitled.model.dto.auth.SignonDto;
import com.joje.untitled.model.dto.auth.SignupDto;
import com.joje.untitled.model.dto.auth.TokenDto;
import com.joje.untitled.model.dto.auth.UserDto;
import com.joje.untitled.model.entity.auth.RoleEntity;
import com.joje.untitled.model.entity.auth.TokenEntity;
import com.joje.untitled.model.entity.auth.UserEntity;
import com.joje.untitled.repository.auth.TokenRepository;
import com.joje.untitled.repository.auth.UserRepository;
import com.joje.untitled.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service("AuthService")
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    /**
     * 아이디 중복 체크
     */
    @Override
    public boolean idDuplicateCheck(String userId) {
        return userRepository.countByUserId(userId) == 0;
    }

    /**
     * 회원 가입
     */
    @Override
    @Transactional
    public UserDto signup(SignupDto param) {

        if(userRepository.countByUserId(param.getUserId()) > 0)
            throw new RuntimeException("이미 가입된 유저입니다. ");

        List<RoleEntity> roles = new ArrayList<>();
        roles.add(new RoleEntity(RoleType.ROLE_USER));

        UserEntity user = UserEntity.builder()
                .userId(param.getUserId())
                .userName(param.getUserName())
                .password(passwordEncoder.encode(param.getPassword()))
                .roles(roles)
                .regDate(LocalDateTime.now())
                .enabled(true)
                .build();

        user = userRepository.save(user);

        return modelMapper.map(user, UserDto.class);
    }

    /**
     * 아이디를 이용해 유저 정보 조회
     */
    @Override
    public UserDto getUser(String userId) {
        return modelMapper.map(userRepository.findByUserId(userId).orElse(null), UserDto.class);
    }

    /**
     * 로그인 : 액세스 토큰, 리프레시 토큰 발급
     */
    @Override
    @Transactional
    public TokenDto signon(SignonDto param) {

        String accessToken = "";
        String refreshToken = "";

        try{
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(param.getUserId(), param.getPassword());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            //        토큰 생성
            accessToken = jwtTokenProvider.createAccessToken(authentication);
            refreshToken = jwtTokenProvider.createRefreshToken(authentication);
        } catch (BadCredentialsException e) {
            throw new SignonException("일치하는 로그인 정보가 없음");
        }

        TokenEntity token = TokenEntity.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .userNo(userRepository.findByUserId(param.getUserId()).orElseThrow().getUserNo())
                .createDate(LocalDateTime.now())
                .build();

//        토큰 정보 DB에 저장
        tokenRepository.save(token);

        return new TokenDto(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public void signout(TokenDto param) {
        tokenRepository.deleteById(param.getRefreshToken());
    }

    @Override
    public TokenDto relayToken(TokenDto param) {

        String refreshToken = param.getRefreshToken();
        String accessToken = param.getAccessToken();

        try{
            jwtTokenProvider.validateToken(refreshToken);
        } catch (ExpiredTokenException e){
            throw new ExpiredRefreshTokenException("만료된 Refresh Token");
        }

        TokenEntity token = tokenRepository.findById(refreshToken).orElse(null);
        log.debug("[token]=[{}]", token);

        if (token == null) {
//        디비에 토큰 정보가 없을 시 재 로그인 요청
            throw new ExpiredRefreshTokenException("로그아웃된 토큰");
        } else if (!token.getAccessToken().equals(accessToken)) {
            tokenRepository.delete(token);
            throw new InvalidTokenException("일치하지 않는 토큰");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        int refreshTokenExpire = jwtTokenProvider.getRefreshTokenExpire();

//        토큰 남은 유효기간 구하기
        LocalDateTime tokenCreateDate = token.getCreateDate();
        long currentRefreshTokenExpire = Duration.between(tokenCreateDate, LocalDateTime.now()).getSeconds();

//        Refresh 토큰 만료 기간이 50% 초과했을 경우 재발급
        if (currentRefreshTokenExpire > refreshTokenExpire / 2) {
            tokenRepository.deleteById(refreshToken);
            refreshToken = jwtTokenProvider.createRefreshToken(authentication);
            tokenCreateDate = LocalDateTime.now();
        }

        accessToken = jwtTokenProvider.createAccessToken(authentication);
        TokenEntity updatedToken = TokenEntity.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .userNo(token.getUserNo())
                .createDate(tokenCreateDate)
                .build();

        tokenRepository.save(updatedToken);

        return new TokenDto(accessToken, refreshToken);
    }

}
