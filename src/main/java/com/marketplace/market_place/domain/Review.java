package com.marketplace.market_place.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // New import

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter // Add @Setter here
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob // 긴 텍스트를 위한 어노테이션
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int rating; // 1점에서 5점까지의 별점

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 성능 최적화
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 리뷰를 작성한 유저

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "store_id", nullable = false)
    private Store store; // 리뷰가 달린 가게

    // Review가 사라지면 관련 ReviewImage도 모두 삭제되도록 Cascade 설정
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> images = new ArrayList<>();

    @Builder
    public Review(String content, int rating, User user, Store store) {
        this.content = content;
        this.rating = rating;
        this.user = user;
        this.store = store;
    }

    //== 연관관계 편의 메서드 ==//
    public void addImage(ReviewImage image) {
        images.add(image);
        image.setReview(this);
    }

    // New method to clear images
    public void clearImages() {
        this.images.clear();
    }
}
