package com.marketplace.market_place.api.main.model;

public enum SortType {
    rating,        // 평점순
    reviewCount,   // 리뷰많은순
    distance       // 거리순 (lat/lon 있을 때만 의미 있음)
}