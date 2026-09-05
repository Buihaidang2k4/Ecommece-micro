package com.myshop.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CouponRequest {
    @NotBlank
    private String code;

    @NotNull
    private Integer scope;

    @NotNull
    private Integer discountType;

    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderValue;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private Boolean enabled;
    private Integer usageLimit;
    private Integer limitPerUser;
    private Integer maxUsesPerUser;
}
