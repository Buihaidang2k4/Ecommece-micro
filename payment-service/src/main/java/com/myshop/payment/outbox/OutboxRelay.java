package com.myshop.payment.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myshop.commons.events.DomainEventType;
import com.myshop.payment.entity.OutboxEvent;
import com.myshop.payment.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "myshop.outbox.relay-enabled", havingValue = "true", matchIfMissing = true)
public class OutboxRelay {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${myshop.outbox.relay-delay-ms:1000}")
    @Transactional
    public void pollAndPublish() {
        List<OutboxEvent> events = outboxRepository.findTop50ByPublishedFalseOrderByCreatedAtAsc();
        for (OutboxEvent event : events) {
            try {
                Object payload = deserialize(event);
                kafkaTemplate.send(event.getTopic(), event.getAggregateId(), payload);
                event.setPublished(true);
                outboxRepository.save(event);
                log.debug("Relayed outbox event id={} type={} topic={}",
                        event.getId(), event.getEventType(), event.getTopic());
            } catch (Exception e) {
                log.error("Failed to relay outbox event id={}: {}", event.getId(), e.getMessage());
                break;
            }
        }
    }

    private Object deserialize(OutboxEvent event) throws Exception {
        DomainEventType eventType = DomainEventType.fromType(event.getEventType());
        return objectMapper.readValue(event.getPayload(), eventType.getPayloadClass());
    }
}
