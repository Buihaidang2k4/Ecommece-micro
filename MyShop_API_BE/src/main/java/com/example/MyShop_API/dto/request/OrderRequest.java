package com.example.MyShop_API.dto.request;


import com.example.MyShop_API.Enum.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    @NotNull(message = "Profile id is required")
    Long profileId;
    Long productId;

    @NotNull(message = "Address id is required")
    Long addressId;

    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity; // Use Buy Now

    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod; // VNPAY : CASH
    String bankCode = "NCB"; // VNPAY

    @NotNull(message = "Shipping fee is required")
    @Min(value = 0, message = "Shipping fee cannot be negative")
    BigDecimal shippingFee;
    String couponCode;
    String orderNote;
}
