package com.marketplace.market_place.api.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MainBootstrapResponse {
    private List<String> categories;
    private List<MarketCardDto> markets;
    private List<StoreCardDto> stores;
}