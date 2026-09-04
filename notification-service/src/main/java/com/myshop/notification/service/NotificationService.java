package com.myshop.notification.service;

import com.myshop.commons.events.UserRegisteredEvent;
import com.myshop.notification.document.NotificationLog;
import com.myshop.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationLogRepository repository;
    private final MailNotificationService mailNotificationService;

    public void handleUserRegistered(String eventId, UserRegisteredEvent event) {
        String id = eventId == null || eventId.isBlank() ? UUID.randomUUID().toString() : eventId;
        if (repository.existsByEventId(id)) {
            log.info("Duplicate eventId={}, skip", id);
            return;
        }

        NotificationLog logDoc = NotificationLog.builder()
                .eventId(id)
                .eventType("UserRegistered")
                .userId(event.getUserId())
                .email(event.getEmail())
                .channel("EMAIL")
                .status("PENDING")
                .createdAt(Instant.now())
                .build();
        repository.save(logDoc);

        try {
            mailNotificationService.sendWelcome(event.getEmail());
            logDoc.setStatus("SENT");
            logDoc.setSentAt(Instant.now());
        } catch (Exception e) {
            log.error("Send mail failed for {}: {}", event.getEmail(), e.getMessage());
            logDoc.setStatus("FAILED");
            logDoc.setErrorMessage(e.getMessage());
        }
        repository.save(logDoc);
    }
}
