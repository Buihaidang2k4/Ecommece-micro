package com.myshop.core.controller;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.core.constant.ApiPath;
import com.myshop.core.dto.request.ReviewRequest;
import com.myshop.core.dto.response.ReviewResponse;
import com.myshop.core.service.catalog.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPath.REVIEWS)
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ApiResponse<List<ReviewResponse>> getByProduct(@PathVariable Long productId) {
        return ApiResponse.ok(reviewService.getByProductId(productId));
    }

    @PostMapping
    public ApiResponse<ReviewResponse> create(@Valid @RequestBody ReviewRequest request) {
        return ApiResponse.ok(reviewService.create(request));
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> delete(@PathVariable Long reviewId) {
        reviewService.softDelete(reviewId);
        return ApiResponse.ok(null);
    }
}
