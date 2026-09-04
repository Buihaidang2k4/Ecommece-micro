package com.myshop.auth.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class CookieBearerTokenResolver implements BearerTokenResolver {

    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/api/v1/auth/",
            "/api/v1/users/registration",
            "/actuator/",
            "/swagger-ui/",
            "/v3/api-docs",
            "/swagger-resources/",
            "/webjars/"
    );

    private final String cookieName;

    @Override
    public String resolve(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (isPublicEndpoint(path)) {
            return null;
        }
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith)
                || "/api/v1/users/registration".equals(path);
    }
}
