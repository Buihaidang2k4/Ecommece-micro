package com.myshop.auth.service;

import com.myshop.auth.dto.request.ResetPasswordRequest;
import com.myshop.auth.entity.PasswordResetOtp;
import com.myshop.auth.entity.User;
import com.myshop.auth.repository.PasswordResetOtpRepository;
import com.myshop.auth.repository.UserRepository;
import com.myshop.commons.exception.BusinessException;
import com.myshop.commons.exception.CommonMessageUtils;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetOtpService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void sendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTED));

        otpRepository.deleteAllByUserId(user.getId());

        String otp = generateOtp(6);
        PasswordResetOtp resetOtp = PasswordResetOtp.builder()
                .userId(user.getId())
                .otpCode(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();
        otpRepository.save(resetOtp);

        try {
            emailService.sendOtpMail(user.getEmail(), otp, user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}", email, e);
            throw new BusinessException(
                    ErrorCode.INTERNAL_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.FAILED_SEND_OTP)
            );
        }
        log.info("OTP sent for user {}", user.getId());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTED));

        PasswordResetOtp otpEntity = otpRepository.findByUserIdAndOtpCode(user.getId(), request.getOtp())
                .orElseThrow(() -> new BusinessException(ErrorCode.OTP_INVALID));

        if (otpEntity.isUsed() || otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.OTP_INVALID);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);
        log.info("Password reset successful for user {}", user.getId());
    }

    private String generateOtp(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}
