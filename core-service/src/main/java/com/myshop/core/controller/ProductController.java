package com.myshop.core.controller;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.core.constant.ApiPath;
import com.myshop.core.dto.request.ProductImagePresignRequest;
import com.myshop.core.dto.request.ProductImageRequest;
import com.myshop.core.dto.request.ProductRequest;
import com.myshop.core.dto.response.MediaPresignResponse;
import com.myshop.core.dto.response.ProductImageResponse;
import com.myshop.core.dto.response.ProductResponse;
import com.myshop.core.dto.response.ProductSearchRow;
import com.myshop.core.service.catalog.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(ApiPath.PRODUCTS)
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<List<ProductSearchRow>> search(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice) {
        return ApiResponse.ok(productService.search(name, categoryId, minPrice, maxPrice));
    }

    @GetMapping(ApiPath.PRODUCT_BY_ID)
    public ApiResponse<ProductResponse> getById(@PathVariable("id") Long id) {
        return ApiResponse.ok(productService.getById(id));
    }

    @GetMapping(ApiPath.PRODUCT_BY_SLUG)
    public ApiResponse<ProductResponse> getBySlug(@PathVariable("slug") String slug) {
        return ApiResponse.ok(productService.getBySlug(slug));
    }

    @PostMapping
    public ApiResponse<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.ok(productService.create(request));
    }

    @PutMapping(ApiPath.PRODUCT_BY_ID)
    public ApiResponse<ProductResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody ProductRequest request) {
        return ApiResponse.ok(productService.update(id, request));
    }

    @DeleteMapping(ApiPath.PRODUCT_BY_ID)
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.ok(null);
    }

    @PostMapping(ApiPath.PRODUCT_IMAGES_PRESIGN)
    public ApiResponse<MediaPresignResponse> presignImage(@PathVariable Long id,
                                                          @RequestBody ProductImagePresignRequest request) {
        return ApiResponse.ok(productService.presignImage(id, request));
    }

    @PostMapping(ApiPath.PRODUCT_IMAGES)
    public ApiResponse<ProductImageResponse> addImage(@PathVariable Long id,
                                                      @RequestBody ProductImageRequest request) {
        return ApiResponse.ok(productService.addImage(id, request));
    }

    @DeleteMapping(ApiPath.PRODUCT_IMAGE_BY_ID)
    public ApiResponse<Void> deleteImage(@PathVariable Long imageId) {
        productService.deleteImage(imageId);
        return ApiResponse.ok(null);
    }
}