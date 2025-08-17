package com.marketplace.market_place.api.main.dto;

import lombok.Getter;

@Getter
public class AiSearchRequest {
    private String prompt;     // 필수
    private Double lat;        // 선택
    private Double lon;        // 선택
    private Integer radius;    // 선택(기본 2500)
    private Integer limit;     // 선택(기본 10)
}