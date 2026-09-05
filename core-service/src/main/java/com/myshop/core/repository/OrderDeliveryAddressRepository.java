package com.myshop.core.repository;

import com.myshop.core.entity.order.OrderDeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderDeliveryAddressRepository extends JpaRepository<OrderDeliveryAddress, Long> {
    Optional<OrderDeliveryAddress> findByOrderId(Long orderId);
}
