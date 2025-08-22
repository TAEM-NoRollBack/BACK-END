package com.marketplace.market_place.api.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreCardDto {
    private Long id;
    private String name;
    private String marketName;   // 소속 시장명
    private Double lat;
    private Double lon;
    private Double rating;
    private Integer reviewCount;
    private String thumbnail;
}