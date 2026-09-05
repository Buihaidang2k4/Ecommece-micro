package com.myshop.commons.constants;

public final class KafkaConstants {

    private KafkaConstants() {
    }

    public static final String CONSUMER_GROUP_CORE = "core-service";
    public static final String CONSUMER_GROUP_NOTIFICATION = "notification-service";

    public static final String JSON_TYPE_PAYMENT_SUCCEEDED =
            "spring.json.value.default.type=com.myshop.commons.events.PaymentSucceededEvent";
    public static final String JSON_TYPE_PAYMENT_FAILED =
            "spring.json.value.default.type=com.myshop.commons.events.PaymentFailedEvent";
    public static final String JSON_TYPE_PAYMENT_EXPIRED =
            "spring.json.value.default.type=com.myshop.commons.events.PaymentExpiredEvent";
}
