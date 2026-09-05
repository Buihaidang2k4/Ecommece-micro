package com.myshop.core.controller;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.core.constant.ApiPath;
import com.myshop.core.dto.request.InventoryRequest;
import com.myshop.core.dto.response.InventoryResponse;
import com.myshop.core.service.inventory.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.INVENTORY)
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/product/{productId}")
    public ApiResponse<InventoryResponse> getByProductId(@PathVariable Long productId) {
        return ApiResponse.ok(inventoryService.getByProductId(productId));
    }

    @PostMapping
    public ApiResponse<InventoryResponse> setAvailable(@Valid @RequestBody InventoryRequest request) {
        return ApiResponse.ok(inventoryService.setAvailable(request));
    }
}
