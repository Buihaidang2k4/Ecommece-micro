package com.myshop.auth.config;

import com.myshop.auth.constant.ApiPath;
import com.myshop.commons.constants.SecurityConstants;
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
            ApiPath.AUTH_PREFIX,
            ApiPath.USER_REGISTRATION,
            SecurityConstants.ACTUATOR_PREFIX,
            SecurityConstants.SWAGGER_UI_PREFIX,
            SecurityConstants.V3_API_DOCS_PREFIX,
            SecurityConstants.SWAGGER_RESOURCES_PREFIX,
            SecurityConstants.WEBJARS_PREFIX
    );

    private final String cookieName;

    @Override
    public String resolve(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (isPublicEndpoint(path)) {
            return null;
        }
        if (request.getCookies() != null) {
            String fromCookie = Arrays.stream(request.getCookies())
                    .filter(c -> cookieName.equals(c.getName()))
                    .map(Cookie::getValue)
                    .filter(StringUtils::hasText)
                    .findFirst()
                    .orElse(null);
            if (fromCookie != null) {
                return fromCookie;
            }
        }
        String header = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header)
                && header.regionMatches(true, 0, SecurityConstants.BEARER_PREFIX, 0, SecurityConstants.BEARER_PREFIX.length())) {
            return header.substring(SecurityConstants.BEARER_PREFIX.length()).trim();
        }
        return null;
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith)
                || ApiPath.USER_REGISTRATION.equals(path);
    }
}
