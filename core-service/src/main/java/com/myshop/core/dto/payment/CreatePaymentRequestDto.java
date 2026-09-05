package com.myshop.core.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequestDto {
    private Long orderId;
    private Integer paymentMethod;
    private BigDecimal amount;
    private String bankCode;
}
