package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.dto.LikeToggleResponse;
import com.marketplace.market_place.api.main.service.PostLikeService;
import com.marketplace.market_place.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostLikeController {

    private final PostLikeService postLikeService;

    /**
     * 좋아요 토글 (등록/취소) - PROD 버전 (세션 기반)
     */
    @PostMapping("/{postId}/likes")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        LikeToggleResponse response = postLikeService.toggleLike(postId, user.getId());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    /**
     * 좋아요 수 조회
     */
    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Map<String, Object>> likeCount(@PathVariable Long postId) {
        long count = postLikeService.countLikes(postId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                        "postId", postId,
                        "likeCount", count
                )
        ));
    }
}
