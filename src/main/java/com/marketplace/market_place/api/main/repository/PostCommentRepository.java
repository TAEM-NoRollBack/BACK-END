package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    // 특정 게시글의 댓글 조회 (최신순)
    List<PostComment> findByPostIdOrderByIdDesc(Long postId);

    // 특정 사용자의 댓글 조회
    List<PostComment> findByUserIdOrderByIdDesc(Long userId);
}
