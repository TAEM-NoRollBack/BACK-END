package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    // 게시글의 이미지 목록 조회 (표시 순서별)
    List<PostImage> findByPostIdOrderByDisplayOrder(Long postId);

    // 게시글의 이미지 개수
    long countByPostId(Long postId);
}
