package com.myshop.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;

    public void sendOtpMail(String to, String otp, String username) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        if (from != null && !from.isBlank()) {
            helper.setFrom(from);
        }
        helper.setTo(to);
        helper.setSubject("MyShop - Password reset OTP");

        String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height:1.6;">
                    <h2 style="color:#2E86C1;">Hello %s,</h2>
                    <p>You requested a <strong>password reset</strong> for your MyShop account.</p>
                    <p style="font-size: 18px; font-weight: bold; background-color: #f2f2f2; padding: 10px; display: inline-block; border-radius: 5px;">
                        Your OTP: %s
                    </p>
                    <p>This OTP is valid for <strong>5 minutes</strong>.</p>
                    <p>If you did not request this, please ignore this email.</p>
                    <br>
                    <p>Best regards,<br>MyShop Team</p>
                </body>
                </html>
                """.formatted(username, otp);

        helper.setText(htmlContent, true);
        mailSender.send(message);
        log.info("OTP email sent to {}", to);
    }
}
