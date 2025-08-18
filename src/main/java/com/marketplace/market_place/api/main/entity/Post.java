package com.marketplace.market_place.api.main.entity;

import com.marketplace.market_place.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    // 임시저장 여부
    @Column(name = "is_draft")
    private Boolean isDraft = false;

    // 생성일시
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 수정일시
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> comments = new ArrayList<>();

    @Builder
    public Post(User user, String title, String location, Integer rating, String content, Boolean isPublic, Boolean isDraft) {
        this.user = user;
        this.title = title;
        this.location = location;
        this.rating = rating;
        this.content = content;
        this.isPublic = isPublic != null ? isPublic : true;
        this.isDraft = isDraft != null ? isDraft : false;
    }

    // 편의 메서드
    public void addImage(PostImage image) {
        images.add(image);
        image.setPost(this);
    }

    public void addLike(PostLike like) {
        likes.add(like);
        like.setPost(this);
    }

    public void addComment(PostComment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    // 좋아요 수 계산
    public int getLikeCount() {
        return likes != null ? likes.size() : 0;
    }

    // 댓글 수 계산
    public int getCommentCount() {
        return comments != null ? comments.size() : 0;
    }

    // 썸네일 이미지 URL 가져오기 (첫 번째 이미지)
    public String getThumbnailUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0).getImageUrl();
        }
        return null;
    }
}