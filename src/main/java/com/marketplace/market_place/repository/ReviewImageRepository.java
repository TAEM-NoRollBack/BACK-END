package com.marketplace.market_place.repository;

import com.marketplace.market_place.domain.Review;
import com.marketplace.market_place.domain.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findByReview(Review review);
}
