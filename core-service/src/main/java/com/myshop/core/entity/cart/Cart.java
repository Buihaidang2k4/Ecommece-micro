package com.myshop.core.entity.cart;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "carts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "profile_id", nullable = false, unique = true)
    private Long profileId;

    @Column(name = "total_price", precision = 19, scale = 2)
    private BigDecimal totalPrice;
}
