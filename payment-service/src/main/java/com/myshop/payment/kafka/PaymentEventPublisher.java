package com.myshop.payment.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myshop.commons.events.DomainEventType;
import com.myshop.commons.events.PaymentExpiredEvent;
import com.myshop.commons.events.PaymentFailedEvent;
import com.myshop.commons.events.PaymentSucceededEvent;
import com.myshop.payment.entity.OutboxEvent;
import com.myshop.payment.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${myshop.kafka.payment-succeeded-topic}")
    private String succeededTopic;

    @Value("${myshop.kafka.payment-failed-topic}")
    private String failedTopic;

    @Value("${myshop.kafka.payment-expired-topic}")
    private String expiredTopic;

    @Transactional(propagation = Propagation.MANDATORY)
    public void publishSucceeded(PaymentSucceededEvent event) {
        saveOutbox("Payment", String.valueOf(event.getPaymentId()),
                DomainEventType.PAYMENT_SUCCEEDED, succeededTopic, event);
        log.info("Saved outbox {} for orderId={}", DomainEventType.PAYMENT_SUCCEEDED.getType(), event.getOrderId());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void publishFailed(PaymentFailedEvent event) {
        saveOutbox("Payment", String.valueOf(event.getPaymentId()),
                DomainEventType.PAYMENT_FAILED, failedTopic, event);
        log.info("Saved outbox {} for orderId={}", DomainEventType.PAYMENT_FAILED.getType(), event.getOrderId());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void publishExpired(PaymentExpiredEvent event) {
        saveOutbox("Payment", String.valueOf(event.getPaymentId()),
                DomainEventType.PAYMENT_EXPIRED, expiredTopic, event);
        log.info("Saved outbox {} for orderId={}", DomainEventType.PAYMENT_EXPIRED.getType(), event.getOrderId());
    }

    private void saveOutbox(String aggregateType, String aggregateId,
                            DomainEventType eventType, String topic, Object payload) {
        try {
            OutboxEvent outbox = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType.getType())
                    .payload(objectMapper.writeValueAsString(payload))
                    .topic(topic)
                    .published(false)
                    .createdAt(Instant.now())
                    .build();
            outboxRepository.save(outbox);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize outbox payload for {}: {}", eventType.getType(), e.getMessage());
            throw new RuntimeException("Failed to serialize outbox payload", e);
        }
    }
}
