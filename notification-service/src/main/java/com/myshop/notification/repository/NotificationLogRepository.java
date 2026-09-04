package com.myshop.notification.repository;

import com.myshop.notification.document.NotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NotificationLogRepository extends MongoRepository<NotificationLog, String> {
    Optional<NotificationLog> findByEventId(String eventId);
    boolean existsByEventId(String eventId);
}
