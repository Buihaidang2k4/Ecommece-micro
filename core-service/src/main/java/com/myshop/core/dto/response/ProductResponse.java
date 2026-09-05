package com.myshop.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse implements Serializable {
    private Long productId;
    private Long categoryId;
    private String productName;
    private String description;
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
    private Integer soldCount;
    private Integer reviewCount;
    private Double avgRating;
    private List<ProductImageResponse> images;
}
