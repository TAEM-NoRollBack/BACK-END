package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.dto.MainBootstrapResponse;
import com.marketplace.market_place.api.main.dto.MarketCardDto;
import com.marketplace.market_place.api.main.dto.StoreCardDto;
import com.marketplace.market_place.api.main.entity.Market;
import com.marketplace.market_place.api.main.entity.ApiStore;
import com.marketplace.market_place.api.main.repository.MarketRepository;
import com.marketplace.market_place.api.main.repository.ApiStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {

    private final MarketRepository marketRepository;
    private final ApiStoreRepository apiStoreRepository;

    @Transactional(readOnly = true)
    public MainBootstrapResponse bootstrap(double lat, double lon) {

        // 1) 카테고리 (일단 고정값)
        List<String> categories = List.of(
                "내 주변 전통시장",
                "내 주변 맛집",
                "점심식사",
                "신규 맛집",
                "을지대 맛집"   // 네 프로젝트에 맞게 라벨 조정
        );

        // 2) DB에서 시장/가게 조회 (평점순 상위 10개)
        List<Market> markets = marketRepository.findTop10ByOrderByRatingDesc();
        List<ApiStore> apiStores = apiStoreRepository.findTop10ByOrderByRatingDesc();

        // 3) DTO 매핑
        List<MarketCardDto> marketDtos = markets.stream()
                .map(m -> new MarketCardDto(
                        m.getId(),
                        m.getName(),
                        m.getAddress(),
                        m.getLat(),
                        m.getLon(),
                        nvl(m.getRating(), 0.0),
                        nvl(m.getReviewCount(), 0),
                        m.getThumbnail()
                ))
                .toList();

        List<StoreCardDto> storeDtos = apiStores.stream()
                .map(s -> new StoreCardDto(
                        s.getId(),
                        s.getName(),
                        s.getMarket() != null ? s.getMarket().getName() : null, // 소속 시장명
                        s.getLat(),
                        s.getLon(),
                        nvl(s.getRating(), 0.0),
                        nvl(s.getReviewCount(), 0),
                        s.getThumbnail()
                ))
                .toList();

        return new MainBootstrapResponse(categories, marketDtos, storeDtos);
    }

    // null 안전 처리(평점/리뷰수가 null이어도 NPE 방지)
    private static Double nvl(Double v, double d) { return v != null ? v : d; }
    private static Integer nvl(Integer v, int d) { return v != null ? v : d; }
}