package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
