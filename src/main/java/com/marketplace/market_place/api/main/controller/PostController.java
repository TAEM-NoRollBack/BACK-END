package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.entity.Post;
import com.marketplace.market_place.api.main.service.PostService;
import com.marketplace.market_place.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 글 작성 (+200 리워드) - 텍스트만 (application/json)
    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody Post post,
                                        @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        post.setUser(user);
        Post savedPost = postService.savePost(post);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "게시글 작성 완료 (+200 리워드)",
                "data", savedPost
        ));
    }

    // 글 작성 (+200 리워드) - 텍스트 + 이미지 동시 업로드 (multipart/form-data)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPostWithImages(@RequestPart("post") @Valid Post post,
                                                  @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                                  @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        Post savedPost = postService.savePostWithImages(post, images, user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "게시글 작성 완료 (+200 리워드)",
                "data", savedPost
        ));
    }

    // 글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId,
                                        @Valid @RequestBody Post post,
                                        @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        Optional<Post> updated = postService.updatePost(postId, post, user);
        if (updated.isEmpty()) {
            return ResponseEntity.status(403).body(Map.of("error", "권한이 없거나 글이 존재하지 않습니다."));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "게시글 수정 완료",
                "data", updated.get()
        ));
    }

    // 글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,
                                        @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        boolean deleted = postService.deletePost(postId, user);
        if (!deleted) {
            return ResponseEntity.status(403).body(Map.of("error", "권한이 없거나 글이 존재하지 않습니다."));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "게시글 삭제 완료"
        ));
    }

    // 글 상세 조회 (likeCount + images 포함)
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId,
                                     @AuthenticationPrincipal User user) {
        Optional<Post> post = postService.findByIdWithAccessCheck(postId, user);
        if (post.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "글을 찾을 수 없거나 권한이 없습니다."));
        }

        Post p = post.get();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                        "postId", p.getId(),
                        "title", p.getTitle(),
                        "location", p.getLocation(),
                        "rating", p.getRating(),
                        "content", p.getContent(),
                        "author", p.getUser().getNickname(),
                        "likeCount", p.getLikes().size(),
                        "images", p.getImages().stream()
                                .map(img -> Map.of(
                                        "imageId", img.getId(),
                                        "imageUrl", img.getImageUrl()
                                ))
                                .toList()
                )
        ));
    }

    // 공개 글 리스트 조회 (likeCount + images 포함)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPublicPosts() {
        List<Post> posts = postService.findPublicPosts();

        List<Map<String, Object>> responsePosts = posts.stream()
                .map(p -> Map.of(
                        "postId", p.getId(),
                        "title", p.getTitle(),
                        "location", p.getLocation(),
                        "rating", p.getRating(),
                        "content", p.getContent(),
                        "author", p.getUser().getNickname(),
                        "likeCount", p.getLikes().size(),
                        "images", p.getImages().stream()
                                .map(img -> Map.of(
                                        "imageId", img.getId(),
                                        "imageUrl", img.getImageUrl()
                                ))
                                .toList()
                ))
                .toList();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", responsePosts
        ));
    }

    // 임시저장 글 리스트 조회 (본인)
    @GetMapping("/drafts")
    public ResponseEntity<?> getDrafts(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        List<Post> drafts = postService.findDraftsByUser(user);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", drafts
        ));
    }
}
