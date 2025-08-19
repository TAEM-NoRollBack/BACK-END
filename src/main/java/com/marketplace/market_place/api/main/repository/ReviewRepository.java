package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.Review;
import com.marketplace.market_place.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserOrderByIdDesc(User user);
}
