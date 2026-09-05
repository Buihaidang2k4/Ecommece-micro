package com.myshop.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "payment_method")
    private Integer paymentMethod;

    @Column(name = "payment_status")
    private Integer paymentStatus;

    @Column(name = "vnp_txn_ref", length = 64, unique = true)
    private String vnpTxnRef;

    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "order_info", length = 255)
    private String orderInfo;

    @Column(name = "bank_code", length = 64)
    private String bankCode;

    @Column(name = "response_code", length = 32)
    private String responseCode;

    @Column(name = "status", length = 64)
    private String status;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "transaction_no", length = 128)
    private String transactionNo;

    @Column(name = "card_type", length = 64)
    private String cardType;
}
