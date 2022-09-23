package com.joje.untitled.common.security;

import com.joje.untitled.exception.ExpiredTokenException;
import com.joje.untitled.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider implements InitializingBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORITIES = "auth";

    private final String secret;
    private final int ACCESS_TOKEN_VALIDATiON_SECOND;
    private final int REFRESH_TOKEN_VALIDATiON_SECOND;

    private Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.access-token-expire}") int accessTokenExpire,
                            @Value("${jwt.refresh-token-expire}") int refreshTokenExpire) {

        this.secret = secret;
        this.ACCESS_TOKEN_VALIDATiON_SECOND = accessTokenExpire;
        this.REFRESH_TOKEN_VALIDATiON_SECOND = refreshTokenExpire;
    }

    /**
     * 빈이 생성이 되고 의존성 주입이 되고 난 후에 주입받은 secret 값을 Base64 Decode 해서 key 변수 할당
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Authentication 객체의 권한정보를 이용해서 액세스 토큰을 생성
     */
    public String createAccessToken(Authentication authentication) {
        return this.generateToken(authentication, this.ACCESS_TOKEN_VALIDATiON_SECOND * 1000L);
    }

    /**
     * 리프레시 토큰 발급
     */
    public String createRefreshToken(Authentication authentication) {
        return this.generateToken(authentication, this.REFRESH_TOKEN_VALIDATiON_SECOND * 1000L);
    }

    private String generateToken(Authentication authentication, Long expireTime){
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

//        유효 기간
        Date validity = new Date(System.currentTimeMillis() + expireTime);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    /**
     * token에 담겨있는 정보를 이용해 Authentication 객체를 리턴하는 메소드 생성
     */
    public Authentication getAuthentication(String token) {
        // token을 활용하여 Claims 생성
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // claims과 authorities 정보를 활용해 User (org.springframework.security.core.userdetails.User) 객체 생성
        User principal = new User(claims.getSubject(), "", authorities);

        // Authentication 객체를 리턴
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new InvalidTokenException("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("JWT 토큰이 잘못되었습니다.");
        }
    }

    /**
     * Request Header에서 토큰 정보를 리턴
     * @param request
     * @return
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 토큰에서 유저 아이디 리턴
     * @param request
     * @return
     */
    public long getUserNo(HttpServletRequest request) {
        String token = this.resolveToken(request);
        if (token != null) {
            return Long.parseLong(this.getAuthentication(token).getName());
        }
        throw new InvalidTokenException("JWT토큰에서 유저 아이디 정보를 가져오는데 실패");
    }


    public int getRefreshTokenExpire() {
        return REFRESH_TOKEN_VALIDATiON_SECOND;
    }

}
