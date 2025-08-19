package com.marketplace.market_place.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookmarkResponseDto {
    private Long storeId;
    private boolean isBookmarked;
}
