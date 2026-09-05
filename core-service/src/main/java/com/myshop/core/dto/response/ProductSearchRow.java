package com.myshop.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRow {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private BigDecimal specialPrice;
    private String slug;
    private Long categoryId;
    private Double avgRating;
}
