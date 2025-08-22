package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.dto.MarketCardDto;
import com.marketplace.market_place.api.main.dto.MarketListResponse;
import com.marketplace.market_place.api.main.entity.Market;
import com.marketplace.market_place.api.main.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketService {

    private final MarketRepository marketRepository;

    /**
     * 시장 목록 페이징 조회
     * sort: rating | reviewCount (기본: rating desc)
     */
    @Transactional(readOnly = true)
    public MarketListResponse list(int page, int size, String sort) {
        String s = (sort == null || sort.isBlank()) ? "rating" : sort;
        Sort springSort = switch (s) {
            case "reviewCount" -> Sort.by(Sort.Direction.DESC, "reviewCount");
            // distance 정렬은 나중에(좌표 기반 계산) 붙일 예정
            default -> Sort.by(Sort.Direction.DESC, "rating");
        };

        PageRequest pr = PageRequest.of(page, size, springSort);
        Page<Market> result = marketRepository.findAll(pr);

        List<MarketCardDto> content = result.getContent().stream()
                .map(m -> new MarketCardDto(
                        m.getId(),
                        m.getName(),
                        m.getAddress(),
                        m.getLat(),
                        m.getLon(),
                        m.getRating() != null ? m.getRating() : 0.0,
                        m.getReviewCount() != null ? m.getReviewCount() : 0,
                        m.getThumbnail()
                ))
                .toList();

        return new MarketListResponse(content, result.getNumber(), result.getSize(), result.getTotalElements());
    }
}