package com.myshop.commons.events;

import java.util.Arrays;

public enum DomainEventType {
    USER_REGISTERED("UserRegisteredEvent", UserRegisteredEvent.class),
    ORDER_CREATED("OrderCreatedEvent", OrderCreatedEvent.class),
    PAYMENT_SUCCEEDED("PaymentSucceededEvent", PaymentSucceededEvent.class),
    PAYMENT_FAILED("PaymentFailedEvent", PaymentFailedEvent.class),
    PAYMENT_EXPIRED("PaymentExpiredEvent", PaymentExpiredEvent.class);

    private final String type;
    private final Class<?> payloadClass;

    DomainEventType(String type, Class<?> payloadClass) {
        this.type = type;
        this.payloadClass = payloadClass;
    }

    public String getType() {
        return type;
    }

    public Class<?> getPayloadClass() {
        return payloadClass;
    }

    public static DomainEventType fromType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Event type is required");
        }
        return Arrays.stream(values())
                .filter(e -> e.type.equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown event type: " + type));
    }
}
