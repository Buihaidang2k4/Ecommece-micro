package com.myshop.notification.service;

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
public class MailNotificationService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;

    public void sendWelcome(String toEmail) {
        if (from == null || from.isBlank()) {
            log.warn("Mail username empty — skip sending welcome to {}", toEmail);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            if (!from.isBlank()) {
                helper.setFrom(from);
            }
            helper.setSubject("Welcome to MyShop");
            helper.setText("""
                    <html><body>
                    <h2>Welcome!</h2>
                    <p>Your MyShop account <strong>%s</strong> has been registered successfully.</p>
                    </body></html>
                    """.formatted(toEmail), true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send welcome email: " + e.getMessage(), e);
        }
    }
}
