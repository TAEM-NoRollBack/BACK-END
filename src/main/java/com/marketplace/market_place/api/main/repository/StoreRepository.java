package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface StoreRepository extends JpaRepository<Store, Long> {
    // 평점 높은 순 상위 10개
    List<Store> findTop10ByOrderByRatingDesc();

    // (선택) 특정 시장의 가게들 중 평점 높은 순
    List<Store> findTop10ByMarketIdOrderByRatingDesc(Long marketId);

    Page<Store> findByMarketId(Long marketId, Pageable pageable);
}