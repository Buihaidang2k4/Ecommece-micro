package com.myshop.auth.service;

import com.myshop.commons.events.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${myshop.kafka.user-registered-topic:myshop.user.registered}")
    private String topic;

    public void publish(Long userId, String email) {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(userId)
                .email(email)
                .build();
        try {
            kafkaTemplate.send(topic, String.valueOf(userId), event);
            log.info("Published UserRegisteredEvent for userId={} email={}", userId, email);
        } catch (Exception e) {
            log.error("Failed to publish UserRegisteredEvent for userId={}: {}", userId, e.getMessage());
        }
    }
}
