package com.myshop.core.service.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myshop.commons.constants.enums.CommonEnums.OrderStatus;
import com.myshop.commons.constants.enums.CommonEnums.PaymentMethod;
import com.myshop.commons.dto.ApiResponse;
import com.myshop.commons.events.DomainEventType;
import com.myshop.commons.events.OrderCreatedEvent;
import com.myshop.commons.exception.BusinessException;
import com.myshop.commons.exception.CommonMessageUtils;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
import com.myshop.core.client.PaymentServiceClient;
import com.myshop.core.dto.payment.CreatePaymentRequestDto;
import com.myshop.core.dto.payment.PaymentResponseDto;
import com.myshop.core.dto.request.BuyNowRequest;
import com.myshop.core.dto.request.PlaceOrderRequest;
import com.myshop.core.dto.response.*;
import com.myshop.core.entity.OutboxEvent;
import com.myshop.core.entity.cart.Cart;
import com.myshop.core.entity.cart.CartItem;
import com.myshop.core.entity.catalog.Product;
import com.myshop.core.entity.customer.Address;
import com.myshop.core.entity.order.*;
import com.myshop.core.repository.*;
import com.myshop.core.service.cart.CartService;
import com.myshop.core.service.inventory.InventoryService;
import com.myshop.core.service.promotion.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderDeliveryAddressRepository deliveryAddressRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final InventoryService inventoryService;
    private final PaymentServiceClient paymentServiceClient;
    private final CouponService couponService;
    private final CartService cartService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${myshop.kafka.order-created-topic:myshop.order.created}")
    private String orderCreatedTopic;

    @Transactional
    public OrderResponse buyNow(BuyNowRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Core.PRODUCT_NOT_FOUND)));
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Core.ADDRESS_NOT_FOUND)));

        BigDecimal unitPrice = product.getSpecialPrice() != null ? product.getSpecialPrice() : product.getPrice();
        BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));
        BigDecimal shippingFee = request.getShippingFee() != null ? request.getShippingFee() : BigDecimal.ZERO;

        BigDecimal discountAmount = couponService.applyDiscount(request.getCouponCode(), itemTotal);
        BigDecimal totalAmount = itemTotal.add(shippingFee).subtract(discountAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) totalAmount = BigDecimal.ZERO;

        inventoryService.reserveStock(request.getProductId(), request.getQuantity());

        int paymentMethod = request.getPaymentMethod();

        OrderEntity order = OrderEntity.builder()
                .profileId(request.getProfileId())
                .couponCode(request.getCouponCode())
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .orderStatus(OrderStatus.CREATED)
                .orderDate(LocalDateTime.now())
                .orderNote(request.getOrderNote())
                .build();
        orderRepository.save(order);

        OrderItem orderItem = OrderItem.builder()
                .orderId(order.getOrderId())
                .productId(product.getProductId())
                .quantity(request.getQuantity())
                .price(unitPrice)
                .productName(product.getProductName())
                .build();
        orderItemRepository.save(orderItem);

        snapshotDeliveryAddress(order.getOrderId(), address);

        PaymentResponseDto paymentResp = createPaymentViaFeign(
                order.getOrderId(), paymentMethod, totalAmount, null);
        order.setPaymentId(paymentResp.getPaymentId());
        orderRepository.save(order);

        addStatusHistory(order.getOrderId(), OrderStatus.CREATED, null);
        saveOrderCreatedOutbox(order);

        return buildOrderResponse(order);
    }

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        Cart cart = cartRepository.findByProfileId(request.getProfileId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Core.CART_NOT_FOUND)));
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getCartId());
        if (cartItems.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Core.CART_EMPTY));
        }
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Core.ADDRESS_NOT_FOUND)));

        BigDecimal itemsTotal = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            itemsTotal = itemsTotal.add(ci.getTotalPrice());
        }

        BigDecimal shippingFee = request.getShippingFee() != null ? request.getShippingFee() : BigDecimal.ZERO;
        BigDecimal discountAmount = couponService.applyDiscount(request.getCouponCode(), itemsTotal);
        BigDecimal totalAmount = itemsTotal.add(shippingFee).subtract(discountAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) totalAmount = BigDecimal.ZERO;

        for (CartItem ci : cartItems) {
            inventoryService.reserveStock(ci.getProductId(), ci.getQuantity());
        }

        int paymentMethod = request.getPaymentMethod();

        OrderEntity order = OrderEntity.builder()
                .profileId(request.getProfileId())
                .couponCode(request.getCouponCode())
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .orderStatus(OrderStatus.CREATED)
                .orderDate(LocalDateTime.now())
                .orderNote(request.getOrderNote())
                .build();
        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cartItems) {
            OrderItem oi = OrderItem.builder()
                    .orderId(order.getOrderId())
                    .productId(ci.getProductId())
                    .cartItemId(ci.getCartItemId())
                    .quantity(ci.getQuantity())
                    .price(ci.getUnitPrice())
                    .productName(ci.getProductName())
                    .build();
            orderItems.add(oi);
        }
        orderItemRepository.saveAll(orderItems);

        snapshotDeliveryAddress(order.getOrderId(), address);

        PaymentResponseDto paymentResp = createPaymentViaFeign(
                order.getOrderId(), paymentMethod, totalAmount, null);
        order.setPaymentId(paymentResp.getPaymentId());
        orderRepository.save(order);

        addStatusHistory(order.getOrderId(), OrderStatus.CREATED, null);
        saveOrderCreatedOutbox(order);

        cartService.clearCart(cart.getCartId());

        return buildOrderResponse(order);
    }

    public OrderResponse getById(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Core.ORDER_NOT_FOUND)));
        return buildOrderResponse(order);
    }

    public List<OrderResponse> getByProfileId(Long profileId) {
        return orderRepository.findByProfileId(profileId).stream()
                .map(this::buildOrderResponse)
                .toList();
    }

    @Transactional
    public OrderResponse cancel(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Core.ORDER_NOT_FOUND)));
        if (Objects.equals(order.getOrderStatus(), OrderStatus.SHIPPED)
                || Objects.equals(order.getOrderStatus(), OrderStatus.DELIVERED)) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Core.CANNOT_CANCEL_ORDER));
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            inventoryService.releaseStock(item.getProductId(), item.getQuantity());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        addStatusHistory(orderId, OrderStatus.CANCELLED, null);

        return buildOrderResponse(order);
    }

    private void saveOrderCreatedOutbox(OrderEntity order) {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(order.getOrderId())
                .profileId(order.getProfileId())
                .totalAmount(order.getTotalAmount())
                .occurredAt(Instant.now())
                .build();
        try {
            OutboxEvent outbox = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType("Order")
                    .aggregateId(String.valueOf(order.getOrderId()))
                    .eventType(DomainEventType.ORDER_CREATED.getType())
                    .payload(objectMapper.writeValueAsString(event))
                    .topic(orderCreatedTopic)
                    .published(false)
                    .createdAt(Instant.now())
                    .build();
            outboxRepository.save(outbox);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize " + DomainEventType.ORDER_CREATED.getType(), e);
        }
    }

    private void snapshotDeliveryAddress(Long orderId, Address address) {
        OrderDeliveryAddress da = OrderDeliveryAddress.builder()
                .orderId(orderId)
                .recipientName(address.getFullName())
                .recipientPhone(address.getPhone())
                .street(address.getStreet())
                .ward(address.getWard())
                .district(address.getDistrict())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .createdAt(LocalDateTime.now())
                .build();
        deliveryAddressRepository.save(da);
    }

    private void addStatusHistory(Long orderId, int status, Long changedByUserId) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .orderId(orderId)
                .orderStatus(status)
                .changedByUserId(changedByUserId)
                .changedAt(LocalDateTime.now())
                .build();
        statusHistoryRepository.save(history);
    }

    private OrderResponse buildOrderResponse(OrderEntity order) {
        List<OrderItemResponse> items = orderItemRepository.findByOrderId(order.getOrderId()).stream()
                .map(oi -> OrderItemResponse.builder()
                        .orderItemId(oi.getOrderItemId())
                        .productId(oi.getProductId())
                        .productName(oi.getProductName())
                        .quantity(oi.getQuantity())
                        .price(oi.getPrice())
                        .build())
                .toList();

        AddressResponse deliveryAddr = deliveryAddressRepository.findByOrderId(order.getOrderId())
                .map(da -> AddressResponse.builder()
                        .addressId(da.getId())
                        .fullName(da.getRecipientName())
                        .phone(da.getRecipientPhone())
                        .street(da.getStreet())
                        .ward(da.getWard())
                        .district(da.getDistrict())
                        .province(da.getProvince())
                        .postalCode(da.getPostalCode())
                        .build())
                .orElse(null);

        PaymentResponseDto paymentResp = null;
        if (order.getPaymentId() != null) {
            try {
                ApiResponse<PaymentResponseDto> feignResp = paymentServiceClient.get(order.getPaymentId());
                if (feignResp != null && feignResp.getData() != null) {
                    paymentResp = feignResp.getData();
                }
            } catch (Exception e) {
                log.warn("Failed to fetch payment {} from payment-service: {}",
                        order.getPaymentId(), e.getMessage());
            }
        }

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .profileId(order.getProfileId())
                .paymentId(order.getPaymentId())
                .couponCode(order.getCouponCode())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus() != null
                        ? order.getOrderStatus()
                        : null)
                .orderDate(order.getOrderDate())
                .orderNote(order.getOrderNote())
                .items(items)
                .deliveryAddress(deliveryAddr)
                .payment(paymentResp)
                .build();
    }

    private PaymentResponseDto createPaymentViaFeign(Long orderId, Integer paymentMethod,
                                                      BigDecimal amount, String bankCode) {
        CreatePaymentRequestDto req = CreatePaymentRequestDto.builder()
                .orderId(orderId)
                .paymentMethod(paymentMethod)
                .amount(amount)
                .bankCode(bankCode)
                .build();
        ApiResponse<PaymentResponseDto> resp = paymentServiceClient.create(req);
        if (resp == null || resp.getData() == null) {
            throw new BusinessException(
                    ErrorCode.INTERNAL_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Core.FAILED_CREATE_PAYMENT));
        }
        return resp.getData();
    }
}
