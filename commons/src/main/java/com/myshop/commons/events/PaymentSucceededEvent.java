package com.myshop.commons.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSucceededEvent {
    private Long paymentId;
    private Long orderId;
    private Integer paymentMethod;
    private BigDecimal amount;
    private String transactionNo;
    private Instant occurredAt;
}
