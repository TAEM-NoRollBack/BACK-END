package com.marketplace.market_place.repository;

import com.marketplace.market_place.domain.Review;
import com.marketplace.market_place.domain.Store; // New import
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // New import

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByStore(Store store); // New method
}
