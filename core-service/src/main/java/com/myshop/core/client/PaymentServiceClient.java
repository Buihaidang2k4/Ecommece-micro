package com.myshop.core.client;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.core.constant.ApiPath;
import com.myshop.core.dto.payment.CreatePaymentRequestDto;
import com.myshop.core.dto.payment.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping(ApiPath.PAYMENT_SERVICE)
    ApiResponse<PaymentResponseDto> create(@RequestBody CreatePaymentRequestDto request);

    @GetMapping(ApiPath.PAYMENT_SERVICE_BY_ID)
    ApiResponse<PaymentResponseDto> get(@PathVariable("id") Long id);

    @GetMapping(ApiPath.PAYMENT_SERVICE_BY_ORDER)
    ApiResponse<PaymentResponseDto> getByOrder(@PathVariable("orderId") Long orderId);
}
