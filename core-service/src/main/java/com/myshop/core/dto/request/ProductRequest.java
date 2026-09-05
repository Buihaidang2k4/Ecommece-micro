package com.myshop.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private Long categoryId;

    @NotBlank(message = "Product name is required")
    private String productName;

    private String description;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    private BigDecimal specialPrice;
    private BigDecimal discount;
    private String bio;
    private String slug;
    private Double height;
    private Double length;
    private Double weight;
    private Double width;
    private String origin;
}
