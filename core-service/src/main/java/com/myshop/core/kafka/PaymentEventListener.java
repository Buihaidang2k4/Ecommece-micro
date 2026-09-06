package com.myshop.core.kafka;

import com.myshop.core.constant.CoreKafkaConstants;
import com.myshop.core.constant.CoreEnums.OrderStatus;
import com.myshop.commons.events.PaymentExpiredEvent;
import com.myshop.commons.events.PaymentFailedEvent;
import com.myshop.commons.events.PaymentSucceededEvent;
import com.myshop.core.entity.order.OrderEntity;
import com.myshop.core.entity.order.OrderItem;
import com.myshop.core.entity.order.OrderStatusHistory;
import com.myshop.core.repository.OrderItemRepository;
import com.myshop.core.repository.OrderRepository;
import com.myshop.core.repository.OrderStatusHistoryRepository;
import com.myshop.core.service.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "${myshop.kafka.payment-succeeded-topic:myshop.payment.succeeded}",
            groupId = CoreKafkaConstants.CONSUMER_GROUP,
            properties = {
                    CoreKafkaConstants.JSON_TYPE_PAYMENT_SUCCEEDED
            }
    )
    @Transactional
    public void onPaymentSucceeded(PaymentSucceededEvent event) {
        log.info("Received PaymentSucceededEvent: orderId={}, paymentId={}",
                event.getOrderId(), event.getPaymentId());

        orderRepository.findById(event.getOrderId()).ifPresentOrElse(order -> {
            if (Objects.equals(order.getOrderStatus(), OrderStatus.CREATED)) {
                order.setOrderStatus(OrderStatus.PENDING);
                orderRepository.save(order);
                logStatusChange(order.getOrderId(), OrderStatus.PENDING);
                log.info("Order {} moved to PENDING after payment success", order.getOrderId());
            }
        }, () -> log.warn("Order {} not found for PaymentSucceededEvent", event.getOrderId()));
    }

    @KafkaListener(
            topics = "${myshop.kafka.payment-failed-topic:myshop.payment.failed}",
            groupId = CoreKafkaConstants.CONSUMER_GROUP,
            properties = {
                    CoreKafkaConstants.JSON_TYPE_PAYMENT_FAILED
            }
    )
    @Transactional
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.info("Received PaymentFailedEvent: orderId={}, reason={}",
                event.getOrderId(), event.getReason());

        orderRepository.findById(event.getOrderId()).ifPresentOrElse(order -> {
            log.info("Order {} payment failed — reason: {}. Order stays in {} status",
                    order.getOrderId(), event.getReason(), order.getOrderStatus());
        }, () -> log.warn("Order {} not found for PaymentFailedEvent", event.getOrderId()));
    }

    @KafkaListener(
            topics = "${myshop.kafka.payment-expired-topic:myshop.payment.expired}",
            groupId = CoreKafkaConstants.CONSUMER_GROUP,
            properties = {
                    CoreKafkaConstants.JSON_TYPE_PAYMENT_EXPIRED
            }
    )
    @Transactional
    public void onPaymentExpired(PaymentExpiredEvent event) {
        log.info("Received PaymentExpiredEvent: orderId={}", event.getOrderId());

        orderRepository.findById(event.getOrderId()).ifPresentOrElse(order -> {
            if (Objects.equals(order.getOrderStatus(), OrderStatus.CANCELLED)) {
                log.info("Order {} already cancelled, skipping", order.getOrderId());
                return;
            }

            order.setOrderStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            logStatusChange(order.getOrderId(), OrderStatus.CANCELLED);

            List<OrderItem> items = orderItemRepository.findByOrderId(order.getOrderId());
            for (OrderItem item : items) {
                inventoryService.releaseStock(item.getProductId(), item.getQuantity());
            }

            log.info("Order {} cancelled and inventory released due to payment expiry",
                    order.getOrderId());
        }, () -> log.warn("Order {} not found for PaymentExpiredEvent", event.getOrderId()));
    }

    private void logStatusChange(Long orderId, int status) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .orderId(orderId)
                .orderStatus(status)
                .changedAt(LocalDateTime.now())
                .build();
        statusHistoryRepository.save(history);
    }
}
