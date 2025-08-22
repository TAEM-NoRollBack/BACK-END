package com.marketplace.market_place.dto;

import com.marketplace.market_place.domain.Store;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class StoreDetailResponseDto {

    private final Long storeId;
    private final Long kakaoPlaceId;
    private final String name;
    private final String address;
    private final String phoneNumber;
    private final List<ReviewResponseDto> reviews;
    private final boolean isBookmarked; // New field for bookmark status

    @Builder
    private StoreDetailResponseDto(Long storeId, Long kakaoPlaceId, String name, String address, String phoneNumber, List<ReviewResponseDto> reviews, boolean isBookmarked) {
        this.storeId = storeId;
        this.kakaoPlaceId = kakaoPlaceId;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.reviews = reviews;
        this.isBookmarked = isBookmarked; // Initialize new field
    }

    public static StoreDetailResponseDto from(Store store) {
        // This method will be updated in StoreService to include isBookmarked
        // For now, set to false as a placeholder
        return StoreDetailResponseDto.builder()
                .storeId(store.getId())
                .kakaoPlaceId(store.getKakaoPlaceId())
                .name(store.getName())
                .address(store.getAddress())
                .phoneNumber(store.getPhoneNumber())
                .reviews(store.getReviews().stream()
                        .map(ReviewResponseDto::from)
                        .collect(Collectors.toList()))
                .isBookmarked(false) // Placeholder, will be set correctly in StoreService
                .build();
    }

    // Overloaded from method to include bookmark status
    public static StoreDetailResponseDto from(Store store, boolean isBookmarked) {
        return StoreDetailResponseDto.builder()
                .storeId(store.getId())
                .kakaoPlaceId(store.getKakaoPlaceId())
                .name(store.getName())
                .address(store.getAddress())
                .phoneNumber(store.getPhoneNumber())
                .reviews(store.getReviews().stream()
                        .map(ReviewResponseDto::from)
                        .collect(Collectors.toList()))
                .isBookmarked(isBookmarked)
                .build();
    }
}
