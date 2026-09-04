package com.myshop.auth.repository;

import com.myshop.auth.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    void deleteAllByUserId(Long userId);

    Optional<PasswordResetOtp> findByUserIdAndOtpCode(Long userId, String otpCode);
}
