package com.myshop.core.repository;

import com.myshop.core.entity.catalog.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdAndDeletedFalse(Long productId);
    List<Review> findByProfileId(Long profileId);
}
