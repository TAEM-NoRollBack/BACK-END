package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.dto.*;
import com.marketplace.market_place.api.main.entity.Market;
import com.marketplace.market_place.api.main.entity.ApiStore;
import com.marketplace.market_place.api.main.repository.MarketRepository;
import com.marketplace.market_place.api.main.repository.ApiStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MarketRepository marketRepository;
    private final ApiStoreRepository apiStoreRepository;

    @Transactional(readOnly = true)
    public SearchResponse search(String q, String type, int page, int size, String sort) {
        if (q == null || q.trim().isEmpty()) {
            throw new IllegalArgumentException("q (검색어)는 필수입니다.");
        }
        final String keyword = q.trim();

        String t = (type == null || type.isBlank()) ? "ALL" : type.toUpperCase(Locale.ROOT);
        String s = (sort == null || sort.isBlank()) ? "relevance" : sort.toLowerCase(Locale.ROOT);

        PagedMarkets marketsResult = null;
        PagedStores storesResult = null;

        // MARKET 검색
        if (t.equals("ALL") || t.equals("MARKET")) {
            // DB에서 1차 후보 넉넉히 가져오기 (예: 200개)
            Page<Market> pageResult = marketRepository.findByNameContainingIgnoreCase(keyword, PageRequest.of(0, Math.max(size * 3, 50)));
            List<Market> sorted = switch (s) {
                case "rating" -> pageResult.getContent().stream()
                        .sorted(Comparator.comparingDouble((Market m) -> nz(m.getRating())).reversed()
                                .thenComparing((Market m) -> nzi(m.getReviewCount()), Comparator.reverseOrder())
                                .thenComparing(Market::getName))
                        .toList();
                case "reviewcount" -> pageResult.getContent().stream()
                        .sorted(Comparator.comparingInt((Market m) -> nzi(m.getReviewCount())).reversed()
                                .thenComparing((Market m) -> nz(m.getRating()), Comparator.reverseOrder())
                                .thenComparing(Market::getName))
                        .toList();
                default -> pageResult.getContent().stream()
                        .sorted(Comparator.comparingDouble((Market m) -> -marketScore(keyword, m))
                                .thenComparing((Market m) -> nzi(m.getReviewCount()), Comparator.reverseOrder())
                                .thenComparing((Market m) -> nz(m.getRating()), Comparator.reverseOrder())
                                .thenComparing(Market::getName))
                        .toList();
            };

            // page/size 슬라이스
            List<Market> slice = slice(sorted, page, size);
            List<MarketCardDto> content = slice.stream()
                    .map(m -> new MarketCardDto(
                            m.getId(), m.getName(), m.getAddress(),
                            m.getLat(), m.getLon(),
                            nz(m.getRating()), nzi(m.getReviewCount()), m.getThumbnail()
                    )).toList();
            marketsResult = new PagedMarkets(content, page, size, sorted.size());
        }

        // STORE 검색
        if (t.equals("ALL") || t.equals("STORE")) {
            Page<ApiStore> pageResult = apiStoreRepository.findByNameContainingIgnoreCase(keyword, PageRequest.of(0, Math.max(size * 3, 50)));
            List<ApiStore> sorted = switch (s) {
                case "rating" -> pageResult.getContent().stream()
                        .sorted(Comparator.comparingDouble((ApiStore st) -> nz(st.getRating())).reversed()
                                .thenComparing((ApiStore st) -> nzi(st.getReviewCount()), Comparator.reverseOrder())
                                .thenComparing(ApiStore::getName))
                        .toList();
                case "reviewcount" -> pageResult.getContent().stream()
                        .sorted(Comparator.comparingInt((ApiStore st) -> nzi(st.getReviewCount())).reversed()
                                .thenComparing((ApiStore st) -> nz(st.getRating()), Comparator.reverseOrder())
                                .thenComparing(ApiStore::getName))
                        .toList();
                default -> pageResult.getContent().stream()
                        .sorted(Comparator.comparingDouble((ApiStore st) -> -storeScore(keyword, st))
                                .thenComparing((ApiStore st) -> nzi(st.getReviewCount()), Comparator.reverseOrder())
                                .thenComparing((ApiStore st) -> nz(st.getRating()), Comparator.reverseOrder())
                                .thenComparing(ApiStore::getName))
                        .toList();
            };

            List<ApiStore> slice = slice(sorted, page, size);
            List<StoreCardDto> content = slice.stream()
                    .map(sv -> new StoreCardDto(
                            sv.getId(),
                            sv.getName(),
                            sv.getMarket() != null ? sv.getMarket().getName() : null,
                            sv.getLat(), sv.getLon(),
                            nz(sv.getRating()), nzi(sv.getReviewCount()), sv.getThumbnail()
                    )).toList();
            storesResult = new PagedStores(content, page, size, sorted.size());
        }

        return new SearchResponse(marketsResult, storesResult);
    }

    // ===== 점수 함수 =====

    private double marketScore(String q, Market m) {
        double rel = relevance(q, m.getName());
        double rate = nz(m.getRating());
        double rev = Math.log1p(nzi(m.getReviewCount()));
        // 가중치: 관련도 0.6, 평점 0.2, 리뷰수 0.2
        return 0.6 * rel + 0.2 * rate + 0.2 * rev;
    }

    private double storeScore(String q, ApiStore s) {
        double rel = relevance(q, s.getName());
        double rate = nz(s.getRating());
        double rev = Math.log1p(nzi(s.getReviewCount()));
        return 0.6 * rel + 0.2 * rate + 0.2 * rev;
    }

    /** 이름 관련도: 시작일치=3, 포함=1, 그외=0 */
    private double relevance(String q, String name) {
        if (name == null) return 0.0;
        String a = name.toLowerCase(Locale.ROOT);
        String b = q.toLowerCase(Locale.ROOT);
        if (a.startsWith(b)) return 3.0;
        if (a.contains(b))  return 1.0;
        return 0.0;
    }

    private static Double nz(Double v) { return v != null ? v : 0.0; }
    private static Integer nzi(Integer v) { return v != null ? v : 0; }

    private static <T> List<T> slice(List<T> list, int page, int size) {
        int from = Math.max(0, page * size);
        int to = Math.min(list.size(), from + size);
        if (from >= to) return List.of();
        return list.subList(from, to);
    }
}