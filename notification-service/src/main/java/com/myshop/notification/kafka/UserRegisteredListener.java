package com.myshop.notification.kafka;

import com.myshop.commons.events.UserRegisteredEvent;
import com.myshop.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredListener {
    private final NotificationService notificationService;

    @KafkaListener(topics = "${myshop.kafka.user-registered-topic:myshop.user.registered}", groupId = "notification-service")
    public void onMessage(ConsumerRecord<String, UserRegisteredEvent> record,
                          @Header(value = "kafka_receivedMessageKey", required = false) String key) {
        UserRegisteredEvent event = record.value();
        if (event == null) {
            log.warn("Null UserRegisteredEvent offset={}", record.offset());
            return;
        }
        String eventId = key != null ? key : record.topic() + "-" + record.partition() + "-" + record.offset();
        log.info("Received UserRegistered userId={} email={}", event.getUserId(), event.getEmail());
        notificationService.handleUserRegistered(eventId, event);
    }
}
