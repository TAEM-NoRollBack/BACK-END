package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.dto.PagedMarkets;
import com.marketplace.market_place.api.main.dto.PagedStores;
import com.marketplace.market_place.api.main.service.QuickSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/main/quick")
@RequiredArgsConstructor
public class QuickSearchController {

    private final QuickSearchService quickSearchService;

    @GetMapping("/markets")
    public PagedMarkets markets(
            @RequestParam(defaultValue = "ALL") String filter,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(defaultValue = "2000") int radius,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sort
    ) {
        return quickSearchService.searchMarkets(filter, lat, lon, radius, page, size, sort);
    }

    @GetMapping("/stores")
    public PagedStores stores(
            @RequestParam(defaultValue = "ALL") String filter,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(defaultValue = "2000") int radius,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sort
    ) {
        return quickSearchService.searchStores(filter, lat, lon, radius, page, size, sort);
    }
}