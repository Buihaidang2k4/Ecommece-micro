package com.myshop.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myshop.auth.entity.OutboxEvent;
import com.myshop.auth.repository.OutboxRepository;
import com.myshop.commons.events.DomainEventType;
import com.myshop.commons.events.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredEventPublisher {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${myshop.kafka.user-registered-topic:myshop.user.registered}")
    private String topic;

    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(Long userId, String email) {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(userId)
                .email(email)
                .build();
        try {
            OutboxEvent outbox = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType("User")
                    .aggregateId(String.valueOf(userId))
                    .eventType(DomainEventType.USER_REGISTERED.getType())
                    .payload(objectMapper.writeValueAsString(event))
                    .topic(topic)
                    .published(false)
                    .createdAt(Instant.now())
                    .build();
            outboxRepository.save(outbox);
            log.info("Saved outbox {} for userId={} email={}",
                    DomainEventType.USER_REGISTERED.getType(), userId, email);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize UserRegisteredEvent for userId={}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to serialize outbox payload", e);
        }
    }
}
