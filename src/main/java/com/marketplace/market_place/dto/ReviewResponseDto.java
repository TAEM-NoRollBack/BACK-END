package com.marketplace.market_place.dto;

import com.marketplace.market_place.domain.Review;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReviewResponseDto {

    private final Long reviewId;
    private final Long storeId;
    private final String authorNickname;
    private final String content;
    private final int rating;
    private final List<String> imageUrls;

    @Builder
    private ReviewResponseDto(Long reviewId, Long storeId, String authorNickname, String content, int rating, List<String> imageUrls) {
        this.reviewId = reviewId;
        this.storeId = storeId;
        this.authorNickname = authorNickname;
        this.content = content;
        this.rating = rating;
        this.imageUrls = imageUrls;
    }

    public static ReviewResponseDto from(Review review) {
        return ReviewResponseDto.builder()
                .reviewId(review.getId())
                .storeId(review.getStore().getId())
                .authorNickname(review.getUser().getNickname()) // User 엔티티에 getNickname()이 있다고 가정
                .content(review.getContent())
                .rating(review.getRating())
                .imageUrls(review.getImages().stream()
                        .map(image -> "/images/" + image.getImageUrl()) // 실제 이미지를 서빙할 URL 경로
                        .collect(Collectors.toList()))
                .build();
    }
}
