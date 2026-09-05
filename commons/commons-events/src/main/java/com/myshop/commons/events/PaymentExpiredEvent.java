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
public class PaymentExpiredEvent {
    private Long paymentId;
    private Long orderId;
    private Instant occurredAt;
}
