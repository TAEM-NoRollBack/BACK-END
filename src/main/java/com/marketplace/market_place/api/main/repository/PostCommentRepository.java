package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
}
