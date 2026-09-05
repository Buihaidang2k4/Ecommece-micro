package com.myshop.core.entity.order;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "coupon_code", length = 50)
    private String couponCode;

    @Column(name = "shipping_fee", precision = 19, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "discount_amount", precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "total_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "order_status", nullable = false)
    private Integer orderStatus;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "order_note", length = 500)
    private String orderNote;
}
