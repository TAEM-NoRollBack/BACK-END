package com.marketplace.market_place.api.main.dto;

public record LikeToggleResponse(
        Long postId,
        Long userId,
        boolean liked,
        long likeCount
) { }
