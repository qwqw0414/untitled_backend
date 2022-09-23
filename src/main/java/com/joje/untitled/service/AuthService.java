package com.joje.untitled.service;

import com.joje.untitled.model.dto.auth.SignonDto;
import com.joje.untitled.model.dto.auth.SignupDto;
import com.joje.untitled.model.dto.auth.TokenDto;
import com.joje.untitled.model.dto.auth.UserDto;

import javax.transaction.Transactional;

public interface AuthService {
    boolean idDuplicateCheck(String userId);

    @Transactional
    UserDto signup(SignupDto param);

    UserDto getUser(String userId);

    TokenDto signon(SignonDto param);

    @Transactional
    void signout(TokenDto param);

    @Transactional
    TokenDto relayToken(TokenDto param);
}
