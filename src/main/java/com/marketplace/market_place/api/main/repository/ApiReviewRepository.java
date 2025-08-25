package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.ApiReview;
import com.marketplace.market_place.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiReviewRepository extends JpaRepository<ApiReview, Long> {
    List<ApiReview> findByUserOrderByIdDesc(User user);
}
