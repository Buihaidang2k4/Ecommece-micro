package com.myshop.commons.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainEventEnvelope {
    private String eventId;
    private String eventType;
    private String aggregateType;
    private String aggregateId;
    private String payload;
    private Instant occurredAt;
}
