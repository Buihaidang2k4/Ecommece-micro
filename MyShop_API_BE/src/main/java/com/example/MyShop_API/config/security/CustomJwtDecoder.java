package com.example.MyShop_API.config.security;

import com.example.MyShop_API.dto.request.IntrospectRequest;
import com.example.MyShop_API.service.authentication.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;

@Component
@Slf4j
public class CustomJwtDecoder implements JwtDecoder {

    @Autowired
    private AuthenticationService authenticationService;

    @Value("${jwt.secret}")
    private String signerKey;

    private NimbusJwtDecoder jwtDecoder;

    @PostConstruct
    public void initDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HmacSHA256");
        this.jwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response = authenticationService.introspect(
                    IntrospectRequest.builder().token(token).build()
            );

            if (!response.isValid()) {
                throw new JwtException("Token is invalid or expired");
            }

            return jwtDecoder.decode(token);

        } catch (ParseException | JOSEException e) {
            log.error("Token parsing error: {}", e.getMessage(), e);
            throw new JwtException("Token parsing error: " + e.getMessage());

        } catch (JwtException e) {
            log.error("JWT decoding error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during token decoding: {}", e.getMessage(), e);
            throw new JwtException("Unexpected error: " + e.getMessage());
        }
    }
}