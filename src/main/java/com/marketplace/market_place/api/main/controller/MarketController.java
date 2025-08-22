package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.dto.MarketListResponse;
import com.marketplace.market_place.api.main.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    // 시장 목록 조회 (페이징/정렬)
    @GetMapping("/markets")
    public MarketListResponse listMarkets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sort
            // TODO: 나중에 거리 정렬/반경 필터가 필요하면 lat/lon/radius 파라미터 추가
    ) {
        return marketService.list(page, size, sort);
    }
}