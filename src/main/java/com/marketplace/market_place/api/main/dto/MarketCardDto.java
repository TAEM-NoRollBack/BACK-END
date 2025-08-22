package com.marketplace.market_place.api.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarketCardDto {
    private Long id;
    private String name;
    private String address;
    private Double lat;
    private Double lon;
    private Double rating;
    private Integer reviewCount;
    private String thumbnail;
}