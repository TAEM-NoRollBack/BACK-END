package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.Bookmark;
import com.marketplace.market_place.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserOrderByIdDesc(User user);
}
