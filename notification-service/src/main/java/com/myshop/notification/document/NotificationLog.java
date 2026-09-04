package com.myshop.notification.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class NotificationLog {
    @Id
    private String id;

    @Indexed(unique = true)
    private String eventId;

    private String eventType;
    private Long userId;
    private String email;
    private String channel;
    private String status;
    private String errorMessage;
    private Instant createdAt;
    private Instant sentAt;
}
