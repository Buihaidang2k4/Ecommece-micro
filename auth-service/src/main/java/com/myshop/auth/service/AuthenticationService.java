package com.myshop.auth.service;

import com.myshop.auth.exception.AuthErrorCode;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.myshop.auth.dto.request.GoogleTokenRequest;
import com.myshop.auth.dto.request.LoginRequest;
import com.myshop.auth.dto.response.AuthenticationResponse;
import com.myshop.auth.dto.response.IntrospectResponse;
import com.myshop.auth.entity.Role;
import com.myshop.auth.entity.User;
import com.myshop.auth.entity.UserRole;
import com.myshop.auth.mapper.PermissionMapper;
import com.myshop.auth.mapper.UserQueryMapper;
import com.myshop.auth.repository.RoleRepository;
import com.myshop.auth.repository.UserRepository;
import com.myshop.auth.repository.UserRoleRepository;
import com.myshop.commons.constants.JwtConstants;
import com.myshop.auth.constant.RoleConstants;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.BusinessException;
import com.myshop.auth.constant.AuthMessageKeys;
import com.myshop.commons.exception.MessageHandlerUtils;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionMapper permissionMapper;
    private final UserQueryMapper userQueryMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService blacklistService;
    private final UserRegisteredEventPublisher userRegisteredEventPublisher;

    @Value("${myshop.security.jwt.secret}")
    private String tokenKey;

    @Value("${myshop.security.jwt.expiration-ms}")
    private Long accessTokenDurationMs;

    @Value("${myshop.security.jwt.refreshable-duration-ms}")
    private Long refreshTokenDurationMs;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Transactional
    public AuthenticationResponse authenticate(LoginRequest request) throws ParseException {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(
                        AuthErrorCode.INVALID_CREDENTIALS,
                        MessageHandlerUtils.getMessage(AuthMessageKeys.INVALID_CREDENTIALS)
                ));

        if (!user.isEnabled()) {
            throw new BusinessException(
                    AuthErrorCode.USER_ALREADY_LOCKED,
                    MessageHandlerUtils.getMessage(AuthMessageKeys.USER_ALREADY_LOCKED)
            );
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(
                    AuthErrorCode.PASSWORD_NOT_MATCHES,
                    MessageHandlerUtils.getMessage(AuthMessageKeys.PASSWORD_NOT_MATCHES)
            );
        }

        return issueTokens(user);
    }

    @Transactional
    public AuthenticationResponse authenticateGoogle(GoogleTokenRequest request)
            throws GeneralSecurityException, IOException, ParseException {
        GoogleIdToken idToken = verifyIdToken(request.getToken());
        String email = idToken.getPayload().getEmail();
        if (email == null || email.isBlank()) {
            throw new BusinessException(AuthErrorCode.USER_INVALID);
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> createGoogleUser(email));
        if (!user.isEnabled()) {
            throw new BusinessException(
                    AuthErrorCode.USER_ALREADY_LOCKED,
                    MessageHandlerUtils.getMessage(AuthMessageKeys.USER_ALREADY_LOCKED)
            );
        }
        return issueTokens(user);
    }

    public String refreshToken(String token) throws ParseException, JOSEException {
        blacklistService.validate(token);
        SignedJWT signedJWT = verifyToken(token, true);
        String email = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(
                        AuthErrorCode.USER_NOT_EXISTED,
                        MessageHandlerUtils.getMessage(AuthMessageKeys.USER_NOT_FOUND)
                ));
        if (!user.isEnabled()) {
            throw new BusinessException(
                    AuthErrorCode.USER_ALREADY_LOCKED,
                    MessageHandlerUtils.getMessage(AuthMessageKeys.USER_ALREADY_LOCKED)
            );
        }
        return generateAccessToken(user);
    }

    public IntrospectResponse introspect(String token) throws JOSEException, ParseException {
        if (token == null || token.isBlank()) {
            return IntrospectResponse.builder().valid(false).build();
        }

        Instant exp = null;
        try {
            JWTClaimsSet claimsSet = decodeToken(token);
            if (claimsSet.getExpirationTime() != null) {
                exp = claimsSet.getExpirationTime().toInstant();
            }
            verifyToken(token, false);
            blacklistService.validate(token);
            return IntrospectResponse.builder().valid(true).exp(exp).build();
        } catch (BusinessException e) {
            log.warn("Token introspection failed: {}", e.getMessage());
            return IntrospectResponse.builder().valid(false).exp(exp).build();
        }
    }

    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        try {
            blacklistService.blacklist(refreshToken, Duration.ofMillis(refreshTokenDurationMs));
        } catch (BusinessException e) {
            throw new BusinessException(ErrorCode.TOKEN_REVOKED);
        }
    }

    public ResponseCookie buildCookie(String token, String cookieName, Duration maxAge) {
        return ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAge)
                .build();
    }

    public ResponseCookie clearCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
    }

    public String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public Duration accessCookieMaxAge() {
        return Duration.ofMillis(accessTokenDurationMs);
    }

    public Duration refreshCookieMaxAge() {
        return Duration.ofMillis(refreshTokenDurationMs);
    }

    public JWTClaimsSet decodeToken(String token) throws ParseException {
        return SignedJWT.parse(token).getJWTClaimsSet();
    }

    private AuthenticationResponse issueTokens(User user) throws ParseException {
        blacklistService.revokeAllTokensForUser(user.getId(), Duration.ofMillis(refreshTokenDurationMs));

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        JWTClaimsSet claimsSet = decodeToken(accessToken);
        Instant exp = claimsSet.getExpirationTime().toInstant();

        blacklistService.storeRefreshToken(user.getId(), refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .exp(exp)
                .build();
    }

    private User createGoogleUser(String email) {
        Role userRole = roleRepository.findByRoleName(RoleConstants.USER)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(AuthMessageKeys.USER_ROLE_NOT_FOUND)
                ));

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .enabled(true)
                .build();
        user = userRepository.save(user);
        userRoleRepository.save(UserRole.builder().userId(user.getId()).roleId(userRole.getId()).build());
        userRegisteredEventPublisher.publish(user.getId(), user.getEmail());
        return user;
    }

    private GoogleIdToken verifyIdToken(String token) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance()
        ).setAudience(Collections.singletonList(googleClientId)).build();

        GoogleIdToken idToken = verifier.verify(token);
        if (idToken == null) {
            throw new BusinessException(ErrorCode.UNAUTHENTICATED);
        }
        return idToken;
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(tokenKey.getBytes(StandardCharsets.UTF_8));
        boolean verified = signedJWT.verify(verifier);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        Object typeClaim = signedJWT.getJWTClaimsSet().getClaim(JwtConstants.CLAIM_TYPE);
        String type = typeClaim == null ? null : typeClaim.toString();

        if (!verified) {
            throw new BusinessException(ErrorCode.UNAUTHENTICATED);
        }
        if (expiryTime == null || expiryTime.before(new Date())) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }
        if (isRefresh && !JwtConstants.TYPE_REFRESH.equals(type)) {
            throw new BusinessException(ErrorCode.UNAUTHENTICATED);
        }
        if (!isRefresh && !JwtConstants.TYPE_ACCESS.equals(type)) {
            throw new BusinessException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    private String generateAccessToken(User user) {
        return buildToken(user, Duration.ofMillis(accessTokenDurationMs), JwtConstants.TYPE_ACCESS);
    }

    private String generateRefreshToken(User user) {
        return buildToken(user, Duration.ofMillis(refreshTokenDurationMs), JwtConstants.TYPE_REFRESH);
    }

    private String buildToken(User user, Duration duration, String type) {
        try {
            List<String> permissions = permissionMapper.findPermissionCodesByUserId(user.getId());
            List<String> roleNames = userQueryMapper.findRoleNamesByUserId(user.getId());

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getEmail())
                    .issuer(JwtConstants.ISSUER)
                    .issueTime(new Date())
                    .expirationTime(new Date(Instant.now().plus(duration).toEpochMilli()))
                    .jwtID(UUID.randomUUID().toString())
                    .claim(JwtConstants.CLAIM_USER_ID, user.getId())
                    .claim(JwtConstants.CLAIM_PERMISSIONS, permissions)
                    .claim(JwtConstants.CLAIM_SCOPE, buildScope(roleNames))
                    .claim(JwtConstants.CLAIM_TYPE, type)
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(new MACSigner(tokenKey.getBytes(StandardCharsets.UTF_8)));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new BusinessException(
                    ErrorCode.INTERNAL_ERROR,
                    MessageHandlerUtils.getMessage(AuthMessageKeys.FAILED_CREATE_TOKEN)
            );
        }
    }

    private String buildScope(List<String> roleNames) {
        if (CollectionUtils.isEmpty(roleNames)) {
            return "";
        }
        return roleNames.stream()
                .map(roleName -> JwtConstants.ROLE_PREFIX + roleName)
                .collect(Collectors.joining(" "));
    }
}
