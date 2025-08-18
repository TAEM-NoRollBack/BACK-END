package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
