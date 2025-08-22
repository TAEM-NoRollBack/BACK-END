package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.entity.PostComment;
import com.marketplace.market_place.api.main.service.PostCommentService;
import com.marketplace.market_place.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostCommentController {

    private final PostCommentService service;

    // 특정 게시글의 댓글 조회
    @GetMapping("/{postId}/comments")
    public ResponseEntity<Map<String, Object>> getCommentsByPost(@PathVariable Long postId) {
        List<PostComment> comments = service.findByPostId(postId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", comments
        ));
    }

    // 댓글 등록
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> createComment(@PathVariable Long postId,
                                           @RequestBody Map<String, String> request,
                                           @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        String content = request.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "댓글 내용을 입력해주세요."));
        }

        PostComment comment = service.createComment(postId, user.getId(), content);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "댓글 등록 완료",
                "data", comment
        ));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
                                           @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        boolean deleted = service.deleteComment(commentId, user.getId());
        if (!deleted) {
            return ResponseEntity.status(403).body(Map.of("error", "권한이 없거나 댓글이 존재하지 않습니다."));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "댓글 삭제 완료"
        ));
    }
}
