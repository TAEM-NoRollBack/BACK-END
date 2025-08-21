package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.dto.AiSearchRequest;
import com.marketplace.market_place.api.main.dto.AiSearchResponse;
import com.marketplace.market_place.api.main.service.AiSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/main/ai")
@RequiredArgsConstructor
public class AiSearchController {

    private final AiSearchService aiSearchService;

    // 프론트: 퀵서치의 "AI검색" 버튼 -> AI 검색 화면 -> 이 API에 POST
    @PostMapping("/search")
    public AiSearchResponse search(@RequestBody AiSearchRequest req){
        return aiSearchService.search(req);
    }
}