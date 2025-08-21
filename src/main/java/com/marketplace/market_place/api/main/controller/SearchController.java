package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.dto.SearchResponse;
import com.marketplace.market_place.api.main.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 단어 기반 검색
     * q: 검색어 (필수)
     * type: ALL|MARKET|STORE (기본 ALL)
     * page,size: 페이징 (기본 0, 10)
     * sort: relevance|rating|reviewCount (기본 relevance)
     */
    @GetMapping("/search")
    public SearchResponse search(
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "ALL") String type,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "relevance") String sort
    ) {
        // size 상한(권장): 50
        int safeSize = Math.min(Math.max(size, 1), 50);
        return searchService.search(q, type, page, safeSize, sort);
    }
}