package com.myshop.auth.controller;

import com.myshop.auth.constant.ApiPath;
import com.myshop.auth.dto.request.ForgotPasswordRequest;
import com.myshop.auth.dto.request.GoogleTokenRequest;
import com.myshop.auth.dto.request.LoginRequest;
import com.myshop.auth.dto.request.ResetPasswordRequest;
import com.myshop.auth.dto.response.AuthenticationResponse;
import com.myshop.auth.dto.response.IntrospectResponse;
import com.myshop.auth.service.AuthenticationService;
import com.myshop.auth.service.PasswordResetOtpService;
import com.myshop.commons.constants.SecurityConstants;
import com.myshop.commons.dto.ApiResponse;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

@RestController
@RequestMapping(ApiPath.AUTH)
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;
    private final PasswordResetOtpService passwordResetOtpService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) throws ParseException {
        AuthenticationResponse tokens = authenticationService.authenticate(request);
        addTokenCookies(response, tokens);
        return ResponseEntity.ok(ApiResponse.of(200, "Login successful", tokens.getExp()));
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<Object>> googleLogin(
            @Valid @RequestBody GoogleTokenRequest request,
            HttpServletResponse response
    ) throws GeneralSecurityException, IOException, ParseException {
        AuthenticationResponse tokens = authenticationService.authenticateGoogle(request);
        addTokenCookies(response, tokens);
        return ResponseEntity.ok(ApiResponse.of(200, "Google login successful", tokens.getExp()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Object>> refresh(HttpServletRequest request, HttpServletResponse response)
            throws ParseException, JOSEException {
        String refreshToken = authenticationService.getTokenFromCookie(request, SecurityConstants.REFRESH_COOKIE);
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.of(HttpStatus.UNAUTHORIZED.value(), "No refresh token found", null));
        }
        String newAccessToken = authenticationService.refreshToken(refreshToken);
        ResponseCookie accessCookie = authenticationService.buildCookie(
                newAccessToken,
                SecurityConstants.ACCESS_COOKIE,
                authenticationService.accessCookieMaxAge()
        );
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        return ResponseEntity.ok(ApiResponse.of(200, "Access token refreshed", null));
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(HttpServletRequest request)
            throws ParseException, JOSEException {
        String accessToken = authenticationService.getTokenFromCookie(request, SecurityConstants.ACCESS_COOKIE);
        IntrospectResponse result = authenticationService.introspect(accessToken);
        return ApiResponse.of(200, "Introspect successful", result);
    }

    @PostMapping("/logout")
    public ApiResponse<Object> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = authenticationService.getTokenFromCookie(request, SecurityConstants.REFRESH_COOKIE);
        authenticationService.logout(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE,
                authenticationService.clearCookie(SecurityConstants.ACCESS_COOKIE).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                authenticationService.clearCookie(SecurityConstants.REFRESH_COOKIE).toString());

        return ApiResponse.of(200, "Logout successful", null);
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Boolean> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetOtpService.sendOtp(request.getEmail());
        return ApiResponse.of(200, "OTP has been sent to your email", true);
    }

    @PutMapping("/reset-password")
    public ApiResponse<Boolean> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetOtpService.resetPassword(request);
        return ApiResponse.of(200, "Password reset successful", true);
    }

    private void addTokenCookies(HttpServletResponse response, AuthenticationResponse tokens) {
        ResponseCookie accessCookie = authenticationService.buildCookie(
                tokens.getAccessToken(),
                SecurityConstants.ACCESS_COOKIE,
                authenticationService.accessCookieMaxAge()
        );
        ResponseCookie refreshCookie = authenticationService.buildCookie(
                tokens.getRefreshToken(),
                SecurityConstants.REFRESH_COOKIE,
                authenticationService.refreshCookieMaxAge()
        );
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
