package com.myshop.core.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotNull
    private Long productId;

    @NotNull
    private Long profileId;

    private Long orderId;
    private String customerName;

    @NotNull
    @Min(1) @Max(5)
    private Integer rating;

    private String comment;
}
