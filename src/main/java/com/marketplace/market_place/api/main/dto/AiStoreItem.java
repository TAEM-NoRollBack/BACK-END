package com.marketplace.market_place.api.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiStoreItem {
    private Long id;
    private String name;
    private String marketName;
    private Double lat;
    private Double lon;
    private Double rating;
    private Integer reviewCount;
    private String thumbnail;
    private Double distance;   // m (있으면)
    private String reason;     // 추천 이유 한 줄
}