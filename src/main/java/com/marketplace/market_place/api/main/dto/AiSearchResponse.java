package com.marketplace.market_place.api.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class AiSearchResponse {
    private String summary;          // 상단 설명 문단
    private List<AiStoreItem> items; // 추천 결과
}