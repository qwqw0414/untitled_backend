package com.joje.untitled.common.filter;

import com.joje.untitled.common.utils.HttpUtils;
import com.joje.untitled.common.utils.ReadableRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Order(value = 1)
@WebFilter(urlPatterns = "/untitled/*")
public class LoggerFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        String contentType = request.getContentType();

        log.debug("=================== >> {} START {} >> ===================", method, uri);
        log.debug("ContentType : {}", contentType);
        if (contentType != null && contentType.startsWith("application/json")) {
            ReadableRequestWrapper requestWrapper = new ReadableRequestWrapper((HttpServletRequest) request);
            log.debug("RequestBody :\n{}", HttpUtils.getBody(requestWrapper));
            chain.doFilter(requestWrapper, response);
        } else {
            chain.doFilter(request, response);
        }
        log.debug("=================== << {} END {} << ===================\n", method, uri);


    }
}
