package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.ApiBookmark;
import com.marketplace.market_place.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiBookmarkRepository extends JpaRepository<ApiBookmark, Long> {
    List<ApiBookmark> findByUserOrderByIdDesc(User user);
}
