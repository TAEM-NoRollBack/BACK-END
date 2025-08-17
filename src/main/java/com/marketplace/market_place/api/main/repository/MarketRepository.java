package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.Market;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MarketRepository extends JpaRepository<Market, Long> {

    // 평점 높은 순 상위 10개
    List<Market> findTop10ByOrderByRatingDesc();

    // (선택) 리뷰 많은 순 상위 10개
    List<Market> findTop10ByOrderByReviewCountDesc();

    Page<Market> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}