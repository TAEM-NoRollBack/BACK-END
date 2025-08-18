package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
