package com.myshop.core.service.promotion;

import com.myshop.commons.constants.enums.CommonEnums.CouponScope;
import com.myshop.commons.constants.enums.CommonEnums.DiscountType;
import com.myshop.commons.exception.AppException;
import com.myshop.commons.exception.BusinessException;
import com.myshop.commons.exception.CommonMessageUtils;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
import com.myshop.core.dto.request.CouponRequest;
import com.myshop.core.dto.response.CouponResponse;
import com.myshop.core.entity.promotion.Coupon;
import com.myshop.core.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public List<CouponResponse> getAll() {
        return couponRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CouponResponse getById(Long id) {
        Coupon c = couponRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Core.COUPON_NOT_FOUND)));
        return toResponse(c);
    }

    @Transactional
    public CouponResponse create(CouponRequest request) {
        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .scope(request.getScope())
                .discountType(request.getDiscountType())
                .discountPercent(request.getDiscountPercent())
                .discountAmount(request.getDiscountAmount())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minOrderValue(request.getMinOrderValue())
                .startDate(request.getStartDate())
                .expiryDate(request.getExpiryDate())
                .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                .usageLimit(request.getUsageLimit())
                .usedCount(0)
                .limitPerUser(request.getLimitPerUser())
                .maxUsesPerUser(request.getMaxUsesPerUser())
                .build();
        couponRepository.save(coupon);
        return toResponse(coupon);
    }

    @Transactional
    public CouponResponse update(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Core.COUPON_NOT_FOUND)));
        coupon.setCode(request.getCode());
        coupon.setScope(request.getScope());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountPercent(request.getDiscountPercent());
        coupon.setDiscountAmount(request.getDiscountAmount());
        coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setStartDate(request.getStartDate());
        coupon.setExpiryDate(request.getExpiryDate());
        if (request.getEnabled() != null) coupon.setEnabled(request.getEnabled());
        if (request.getUsageLimit() != null) coupon.setUsageLimit(request.getUsageLimit());
        if (request.getLimitPerUser() != null) coupon.setLimitPerUser(request.getLimitPerUser());
        if (request.getMaxUsesPerUser() != null) coupon.setMaxUsesPerUser(request.getMaxUsesPerUser());
        couponRepository.save(coupon);
        return toResponse(coupon);
    }

    @Transactional
    public void delete(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Core.COUPON_NOT_FOUND));
        }
        couponRepository.deleteById(id);
    }

    public BigDecimal applyDiscount(String couponCode, BigDecimal orderTotal) {
        if (couponCode == null || couponCode.isBlank()) return BigDecimal.ZERO;

        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Core.COUPON_NOT_FOUND)));

        if (!Boolean.TRUE.equals(coupon.getEnabled())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Core.COUPON_DISABLED));
        }
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Core.COUPON_NOT_ACTIVE));
        }
        if (coupon.getExpiryDate() != null && now.isAfter(coupon.getExpiryDate())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Core.COUPON_EXPIRED));
        }
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Core.COUPON_USAGE_LIMIT));
        }
        if (coupon.getMinOrderValue() != null && orderTotal.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Core.COUPON_MIN_ORDER));
        }

        BigDecimal discount;
        if (Objects.equals(coupon.getDiscountType(), DiscountType.PERCENTAGE)) {
            discount = orderTotal.multiply(coupon.getDiscountPercent()).divide(BigDecimal.valueOf(100));
            if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discount = coupon.getMaxDiscountAmount();
            }
        } else {
            discount = coupon.getDiscountAmount() != null ? coupon.getDiscountAmount() : BigDecimal.ZERO;
        }

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);

        return discount;
    }

    private CouponResponse toResponse(Coupon c) {
        return CouponResponse.builder()
                .couponId(c.getCouponId())
                .code(c.getCode())
                .scope(c.getScope())
                .discountType(c.getDiscountType())
                .discountPercent(c.getDiscountPercent())
                .discountAmount(c.getDiscountAmount())
                .maxDiscountAmount(c.getMaxDiscountAmount())
                .minOrderValue(c.getMinOrderValue())
                .startDate(c.getStartDate())
                .expiryDate(c.getExpiryDate())
                .enabled(c.getEnabled())
                .usageLimit(c.getUsageLimit())
                .usedCount(c.getUsedCount())
                .limitPerUser(c.getLimitPerUser())
                .maxUsesPerUser(c.getMaxUsesPerUser())
                .build();
    }
}
