package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.dto.StoreCardDto;
import com.marketplace.market_place.api.main.dto.StoreListResponse;
import com.marketplace.market_place.api.main.entity.Store;
import com.marketplace.market_place.api.main.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class StoreController {

    private final StoreRepository storeRepository;

    /**
     * 가게 목록 조회 (평점/리뷰수 정렬, 시장별 필터, 페이징)
     * GET /api/main/stores?marketId=&amp;sort=rating|reviewCount&amp;page=0&amp;size=10
     */
    @GetMapping("/stores")
    public StoreListResponse listStores(@RequestParam(required = false) Long marketId,
                                        @RequestParam(defaultValue = "rating") String sort,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {

        Sort springSort = switch (sort) {
            case "reviewCount" -> Sort.by(Sort.Direction.DESC, "reviewCount");
            default -> Sort.by(Sort.Direction.DESC, "rating");
        };

        PageRequest pr = PageRequest.of(page, size, springSort);

        Page<Store> result = (marketId != null)
                ? storeRepository.findByMarketId(marketId, pr)
                : storeRepository.findAll(pr);

        List<StoreCardDto> content = result.getContent().stream()
                .map(s -> new StoreCardDto(
                        s.getId(),
                        s.getName(),
                        s.getMarket() != null ? s.getMarket().getName() : null,
                        s.getLat(),
                        s.getLon(),
                        s.getRating() != null ? s.getRating() : 0.0,
                        s.getReviewCount() != null ? s.getReviewCount() : 0,
                        s.getThumbnail()
                ))
                .toList();

        return new StoreListResponse(content, result.getNumber(), result.getSize(), result.getTotalElements());
    }
}