package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.Post;
import com.marketplace.market_place.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserAndIsDraft(User user, Boolean isDraft);

    // 공개글 최신순 조회
    List<Post> findByIsPublicOrderByCreatedAtDesc(Boolean isPublic);

    // 특정 사용자의 글 조회 (최신순)
    List<Post> findByUserOrderByCreatedAtDesc(User user);
}
