package com.myshop.core.controller;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.core.constant.ApiPath;
import com.myshop.core.dto.request.BuyNowRequest;
import com.myshop.core.dto.request.PlaceOrderRequest;
import com.myshop.core.dto.response.OrderResponse;
import com.myshop.core.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPath.ORDERS)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/buy-now")
    public ApiResponse<OrderResponse> buyNow(@Valid @RequestBody BuyNowRequest request) {
        return ApiResponse.ok(orderService.buyNow(request));
    }

    @PostMapping("/place-order")
    public ApiResponse<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        return ApiResponse.ok(orderService.placeOrder(request));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getById(@PathVariable Long orderId) {
        return ApiResponse.ok(orderService.getById(orderId));
    }

    @GetMapping("/by-profile/{profileId}")
    public ApiResponse<List<OrderResponse>> getByProfile(@PathVariable Long profileId) {
        return ApiResponse.ok(orderService.getByProfileId(profileId));
    }

    @PostMapping("/{orderId}/cancel")
    public ApiResponse<OrderResponse> cancel(@PathVariable Long orderId) {
        return ApiResponse.ok(orderService.cancel(orderId));
    }
}
