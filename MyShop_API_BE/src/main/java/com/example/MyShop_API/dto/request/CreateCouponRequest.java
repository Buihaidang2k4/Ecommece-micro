package com.example.MyShop_API.dto.request;

import com.example.MyShop_API.Enum.CouponScope;
import com.example.MyShop_API.Enum.DiscountType;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCouponRequest {

    String code; // để trống → tự sinh

    @NotNull(message = "scope discount not null")
    CouponScope scope;

    @NotNull(message = "Discount type is required")
    DiscountType discountType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Discount percent must be > 0")
    @DecimalMax(value = "100.0", message = "Discount percent must be <= 100")
    BigDecimal discountPercent;

    @DecimalMin(value = "0.0", inclusive = false, message = "Discount amount must be > 0")
    BigDecimal discountAmount;

    @NotNull(message = "maxDiscountAmount not null")
    @DecimalMin(value = "0.0", message = "Max discount amount must be >= 0")
    BigDecimal maxDiscountAmount;

    @NotNull(message = "minOrderValue not null")
    @DecimalMin(value = "0.0", message = "Min order value must be >= 0")
    BigDecimal minOrderValue;

    @FutureOrPresent(message = "Start date must be now or in the future")
    LocalDateTime startDate;

    @NotNull(message = "expiryDate not null")
    @Future(message = "Expiry date must be in the future")
    LocalDateTime expiryDate;

    Integer usageLimit;           // null = không giới hạn
    boolean limitPerUser = true;  // mặc định bật
    Integer maxUsesPerUser = 1;   // mặc định 1 lần/user

    @Builder.Default
    boolean enabled = true;
    
    List<Long> categoryIds;
    List<Long> productIds;
}
