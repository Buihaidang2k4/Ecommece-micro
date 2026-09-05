package com.myshop.payment.controller;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.payment.constant.ApiPath;
import com.myshop.payment.dto.CreatePaymentRequest;
import com.myshop.payment.dto.PaymentInitResponse;
import com.myshop.payment.dto.PaymentResponse;
import com.myshop.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApiPath.PAYMENT)
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${myshop.frontend.order-confirmed-base-url}")
    private String frontendOrderConfirmedBaseUrl;

    @PostMapping
    public ApiResponse<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
        return ApiResponse.ok(paymentService.createPayment(request));
    }

    @GetMapping(ApiPath.BY_ID)
    public ApiResponse<PaymentResponse> getById(@PathVariable Long paymentId) {
        return ApiResponse.ok(paymentService.getById(paymentId));
    }

    @GetMapping(ApiPath.BY_ORDER)
    public ApiResponse<PaymentResponse> getByOrderId(@PathVariable Long orderId) {
        return ApiResponse.ok(paymentService.getByOrderId(orderId));
    }

    @PostMapping(ApiPath.VNPAY_URL)
    public ApiResponse<PaymentInitResponse> createVnPayRedirect(
            @PathVariable Long paymentId,
            HttpServletRequest httpRequest) {
        return ApiResponse.ok(paymentService.createVnPayRedirect(paymentId, httpRequest));
    }

    @GetMapping(ApiPath.VN_PAY_CALLBACK)
    public ResponseEntity<Void> vnPayCallback(@RequestParam Map<String, String> queryParams) {
        PaymentResponse response = paymentService.handleVnPayCallback(queryParams);

        String redirectUrl = frontendOrderConfirmedBaseUrl + response.getOrderId();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }

    @PostMapping(ApiPath.CONFIRM_COD)
    public ApiResponse<PaymentResponse> confirmCod(@PathVariable Long paymentId) {
        return ApiResponse.ok(paymentService.confirmCod(paymentId));
    }
}
