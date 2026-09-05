package com.myshop.core.dto.response;

import com.myshop.core.dto.payment.PaymentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long profileId;
    private Long paymentId;
    private String couponCode;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private Integer orderStatus;
    private LocalDateTime orderDate;
    private String orderNote;
    private List<OrderItemResponse> items;
    private AddressResponse deliveryAddress;
    private PaymentResponseDto payment;
}
