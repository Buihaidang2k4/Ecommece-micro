package com.myshop.core.entity.promotion;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "coupon_category_ids")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CouponCategoryId.CouponCategoryPK.class)
public class CouponCategoryId {

    @Id
    @Column(name = "coupon_id")
    private Long couponId;

    @Id
    @Column(name = "category_id")
    private Long categoryId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponCategoryPK implements Serializable {
        private Long couponId;
        private Long categoryId;
    }
}
