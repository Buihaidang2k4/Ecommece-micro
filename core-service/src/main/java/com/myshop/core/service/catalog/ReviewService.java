package com.myshop.core.service.catalog;

import com.myshop.commons.exception.BusinessException;
import com.myshop.core.constant.CoreMessageKeys;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
import com.myshop.core.dto.request.ReviewRequest;
import com.myshop.core.dto.response.ReviewResponse;
import com.myshop.core.entity.catalog.Product;
import com.myshop.core.entity.catalog.Review;
import com.myshop.core.repository.ProductRepository;
import com.myshop.core.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public List<ReviewResponse> getByProductId(Long productId) {
        return reviewRepository.findByProductIdAndDeletedFalse(productId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ReviewResponse create(ReviewRequest request) {
        Review review = Review.builder()
                .productId(request.getProductId())
                .profileId(request.getProfileId())
                .orderId(request.getOrderId())
                .customerName(request.getCustomerName())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();
        reviewRepository.save(review);

        updateProductRating(request.getProductId());

        return toResponse(review);
    }

    @Transactional
    public void softDelete(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.REVIEW_NOT_FOUND)
                ));
        review.setDeleted(true);
        reviewRepository.save(review);
        updateProductRating(review.getProductId());
    }

    private void updateProductRating(Long productId) {
        List<Review> activeReviews = reviewRepository.findByProductIdAndDeletedFalse(productId);
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setReviewCount(activeReviews.size());
            double avg = activeReviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            product.setAvgRating(avg);
            productRepository.save(product);
        }
    }

    private ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
                .reviewId(r.getReviewId())
                .productId(r.getProductId())
                .profileId(r.getProfileId())
                .orderId(r.getOrderId())
                .customerName(r.getCustomerName())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
