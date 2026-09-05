package com.myshop.payment.repository;

import com.myshop.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByVnpTxnRef(String vnpTxnRef);

    List<Payment> findByPaymentStatusInAndExpiredAtBefore(
            List<Integer> statuses, LocalDateTime before);
}
