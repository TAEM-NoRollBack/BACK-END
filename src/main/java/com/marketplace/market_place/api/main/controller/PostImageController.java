package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.entity.PostImage;
import com.marketplace.market_place.api.main.service.PostImageService;
import com.marketplace.market_place.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostImageController {

    private final PostImageService postImageService;

    // 게시글 이미지 업로드
    @PostMapping("/{postId}/images")
    public ResponseEntity<?> uploadImages(@PathVariable Long postId,
                                          @RequestParam("files") List<MultipartFile> files,
                                          @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            List<PostImage> uploadedImages = postImageService.uploadImages(postId, files, user.getId());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "이미지 업로드 완료",
                    "data", uploadedImages
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "이미지 업로드 실패"));
        }
    }

    // 게시글의 이미지 목록 조회
    @GetMapping("/{postId}/images")
    public ResponseEntity<Map<String, Object>> getImages(@PathVariable Long postId) {
        List<PostImage> images = postImageService.getImagesByPostId(postId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", images
        ));
    }

    // 이미지 삭제
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId,
                                         @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        boolean deleted = postImageService.deleteImage(imageId, user.getId());
        if (!deleted) {
            return ResponseEntity.status(403).body(Map.of("error", "권한이 없거나 이미지가 존재하지 않습니다."));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "이미지 삭제 완료"
        ));
    }
}
