package com.myshop.core.entity.promotion;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "coupon_product_ids")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CouponProductId.CouponProductPK.class)
public class CouponProductId {

    @Id
    @Column(name = "coupon_id")
    private Long couponId;

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponProductPK implements Serializable {
        private Long couponId;
        private Long productId;
    }
}
