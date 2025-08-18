package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
