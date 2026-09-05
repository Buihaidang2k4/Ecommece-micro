package com.myshop.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long reviewId;
    private Long productId;
    private Long profileId;
    private Long orderId;
    private String customerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
