package com.myshop.core.config;

import com.myshop.commons.constants.SecurityConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignAuthConfig {

    @Bean
    public RequestInterceptor feignAuthInterceptor() {
        return this::applyAuth;
    }

    private void applyAuth(RequestTemplate template) {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return;
        }
        HttpServletRequest request = attrs.getRequest();
        String authorization = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        if (authorization != null && !authorization.isBlank()) {
            template.header(SecurityConstants.AUTHORIZATION_HEADER, authorization);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (SecurityConstants.ACCESS_COOKIE.equals(cookie.getName())) {
                template.header("Cookie", SecurityConstants.ACCESS_COOKIE + "=" + cookie.getValue());
                break;
            }
        }
    }
}
