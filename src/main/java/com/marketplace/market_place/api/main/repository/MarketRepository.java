package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRepository extends JpaRepository<Market, Long> {
}
