package com.myshop.core.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyNowRequest {
    @NotNull
    private Long profileId;

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    private Long addressId;

    @NotNull
    private Integer paymentMethod;

    private BigDecimal shippingFee;
    private String couponCode;
    private String orderNote;
}
