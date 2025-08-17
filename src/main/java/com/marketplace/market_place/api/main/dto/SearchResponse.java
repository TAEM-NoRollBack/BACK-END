package com.marketplace.market_place.api.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor @AllArgsConstructor
public class SearchResponse {
    private PagedMarkets markets; // type=STORE이면 null 가능
    private PagedStores stores;   // type=MARKET이면 null 가능
}