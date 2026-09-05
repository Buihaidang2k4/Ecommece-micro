package com.myshop.core.controller;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.core.constant.ApiPath;
import com.myshop.core.dto.request.CartItemRequest;
import com.myshop.core.dto.response.CartResponse;
import com.myshop.core.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.CARTS)
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/profile/{profileId}")
    public ApiResponse<CartResponse> getByProfile(@PathVariable Long profileId) {
        return ApiResponse.ok(cartService.getByProfileId(profileId));
    }

    @PostMapping("/profile/{profileId}/items")
    public ApiResponse<CartResponse> addItem(@PathVariable Long profileId,
                                             @Valid @RequestBody CartItemRequest request) {
        return ApiResponse.ok(cartService.addItem(profileId, request));
    }

    @PutMapping("/profile/{profileId}/items/{cartItemId}")
    public ApiResponse<CartResponse> updateItem(@PathVariable Long profileId,
                                                @PathVariable Long cartItemId,
                                                @RequestParam int quantity) {
        return ApiResponse.ok(cartService.updateItem(profileId, cartItemId, quantity));
    }

    @DeleteMapping("/profile/{profileId}/items/{cartItemId}")
    public ApiResponse<CartResponse> removeItem(@PathVariable Long profileId,
                                                @PathVariable Long cartItemId) {
        return ApiResponse.ok(cartService.removeItem(profileId, cartItemId));
    }
}
