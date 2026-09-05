package com.myshop.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private Long orderId;
    private Integer paymentMethod;
    private Integer paymentStatus;
    private String vnpTxnRef;
    private BigDecimal amount;
    private String orderInfo;
    private String bankCode;
    private String responseCode;
    private LocalDateTime paymentDate;
    private String transactionNo;
    private String cardType;
}
