package com.marketplace.market_place.api.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarketListResponse {
    private List<MarketCardDto> content;
    private int page;
    private int size;
    private long totalElements;
}