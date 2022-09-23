package com.joje.untitled.common.security;

import com.joje.untitled.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 유효한 자격증명을 제공하지 않고 접근하려 할 때 발생하는 에러 핸들러
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 유효한 자격증명을 제공하지 않고 접근하려 할 때 401
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        throw new UnauthorizedException("유효하지 않은 자격 증명");
    }

}
