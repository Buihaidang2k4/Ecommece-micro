package com.myshop.core.controller;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.core.constant.ApiPath;
import com.myshop.core.dto.request.CouponRequest;
import com.myshop.core.dto.response.CouponResponse;
import com.myshop.core.service.promotion.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPath.COUPONS)
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public ApiResponse<List<CouponResponse>> getAll() {
        return ApiResponse.ok(couponService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<CouponResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(couponService.getById(id));
    }

    @PostMapping
    public ApiResponse<CouponResponse> create(@Valid @RequestBody CouponRequest request) {
        return ApiResponse.ok(couponService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CouponResponse> update(@PathVariable Long id,
                                              @Valid @RequestBody CouponRequest request) {
        return ApiResponse.ok(couponService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        couponService.delete(id);
        return ApiResponse.ok(null);
    }
}
