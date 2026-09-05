package com.myshop.payment.config;

import com.myshop.commons.constants.JwtConstants;
import com.myshop.commons.constants.SecurityConstants;
import com.myshop.payment.constant.ApiPath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ApiPath.VN_PAY_CALLBACK_FULL).permitAll()
                        .requestMatchers(SecurityConstants.ACTUATOR_ALL).permitAll()
                        .requestMatchers(SecurityConstants.SWAGGER_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .bearerTokenResolver(new CookieBearerTokenResolver(SecurityConstants.ACCESS_COOKIE))
                        .jwt(jwt -> jwt
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            Object permissionsClaim = jwt.getClaim(JwtConstants.CLAIM_PERMISSIONS);
            if (permissionsClaim instanceof Collection<?> permissions && !permissions.isEmpty()) {
                authorities.addAll(permissions.stream()
                        .map(Object::toString)
                        .filter(s -> !s.isBlank())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));
                return authorities;
            }

            String scope = jwt.getClaimAsString(JwtConstants.CLAIM_SCOPE);
            if (scope != null && !scope.isBlank()) {
                authorities.addAll(Arrays.stream(scope.split(" "))
                        .filter(s -> !s.isBlank())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));
            }
            return authorities;
        });
        return converter;
    }
}
