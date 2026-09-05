package com.myshop.core.dto.response;

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
public class CouponResponse {
    private Long couponId;
    private String code;
    private Integer scope;
    private Integer discountType;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderValue;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private Boolean enabled;
    private Integer usageLimit;
    private Integer usedCount;
    private Integer limitPerUser;
    private Integer maxUsesPerUser;
}
