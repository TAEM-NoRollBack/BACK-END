package com.marketplace.market_place.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "review_images")
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl; // 이미지 파일 경로 또는 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Builder
    public ReviewImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    //== 연관관계 편의 메서드 ==//
    // Review 엔티티에서 이미지를 추가할 때 호출하여 관계를 설정
    public void setReview(Review review) {
        this.review = review;
    }
}
