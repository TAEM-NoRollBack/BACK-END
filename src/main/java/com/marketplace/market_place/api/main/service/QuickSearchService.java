package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.dto.*;
import com.marketplace.market_place.api.main.model.QuickFilter;
import com.marketplace.market_place.api.main.model.SortType;
import com.marketplace.market_place.api.main.entity.Market;
import com.marketplace.market_place.api.main.entity.ApiStore;
import com.marketplace.market_place.api.main.repository.MarketRepository;
import com.marketplace.market_place.api.main.repository.ApiStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuickSearchService {

    private final MarketRepository marketRepository;
    private final ApiStoreRepository apiStoreRepository;

    private static final double EULJI_LAT = 37.4437;
    private static final double EULJI_LON = 127.1289;

    // ---------- MARKETS ----------
    @Transactional(readOnly = true)
    public PagedMarkets searchMarkets(String filterStr, Double lat, Double lon, int radius,
                                      int page, int size, String sortStr) {

        QuickFilter filter = parseFilter(filterStr);
        SortType sort = parseSort(sortStr);

        List<Market> all = marketRepository.findAll();

        List<Market> filtered = switch (filter) {
            case NEARBY -> filterByRadiusMarket(all, lat, lon, radius);
            case EULJI  -> filterByRadiusMarket(all, EULJI_LAT, EULJI_LON, radius);
            case LUNCH, NEW, ALL -> all;
        };

        Comparator<Market> cmp = switch (sort) {
            case distance -> Comparator.comparingDouble(m -> distanceMeters(lat, lon, m.getLat(), m.getLon()));
            case reviewCount -> Comparator.comparingInt((Market m) -> nzi(m.getReviewCount())).reversed()
                    .thenComparing((Market m) -> nz(m.getRating()), Comparator.reverseOrder());
            default -> Comparator.comparingDouble((Market m) -> nz(m.getRating())).reversed()
                    .thenComparing((Market m) -> nzi(m.getReviewCount()), Comparator.reverseOrder());
        };

        List<Market> sorted = filtered.stream().sorted(cmp).toList();
        List<Market> slice = slice(sorted, page, size);

        List<MarketCardDto> content = slice.stream()
                .map(m -> new MarketCardDto(
                        m.getId(), m.getName(), m.getAddress(),
                        m.getLat(), m.getLon(), nz(m.getRating()),
                        nzi(m.getReviewCount()), m.getThumbnail()
                )).toList();

        return new PagedMarkets(content, page, size, filtered.size());
    }

    // ---------- STORES ----------
    @Transactional(readOnly = true)
    public PagedStores searchStores(String filterStr, Double lat, Double lon, int radius,
                                    int page, int size, String sortStr) {

        QuickFilter filter = parseFilter(filterStr);
        SortType sort = parseSort(sortStr);

        List<ApiStore> all = apiStoreRepository.findAll();

        List<ApiStore> filtered = switch (filter) {
            case NEARBY -> filterByRadiusStore(all, lat, lon, radius);
            case LUNCH  -> filterByRadiusStore(all, lat, lon, radius); // 임시 규칙
            case NEW    -> all.stream()
                    .sorted(Comparator.comparingLong(ApiStore::getId).reversed())
                    .limit(500).toList();
            case EULJI  -> filterByRadiusStore(all, EULJI_LAT, EULJI_LON, radius);
            case ALL    -> all;
        };

        Comparator<ApiStore> cmp = switch (sort) {
            case distance -> Comparator.comparingDouble(s -> distanceMeters(lat, lon, s.getLat(), s.getLon()));
            case reviewCount -> Comparator.comparingInt((ApiStore s) -> nzi(s.getReviewCount())).reversed()
                    .thenComparing((ApiStore s) -> nz(s.getRating()), Comparator.reverseOrder());
            default -> Comparator.comparingDouble((ApiStore s) -> nz(s.getRating())).reversed()
                    .thenComparing((ApiStore s) -> nzi(s.getReviewCount()), Comparator.reverseOrder());
        };

        List<ApiStore> sorted = filtered.stream().sorted(cmp).toList();
        List<ApiStore> slice = slice(sorted, page, size);

        List<StoreCardDto> content = slice.stream()
                .map(sv -> new StoreCardDto(
                        sv.getId(), sv.getName(),
                        sv.getMarket()!=null ? sv.getMarket().getName() : null,
                        sv.getLat(), sv.getLon(),
                        nz(sv.getRating()), nzi(sv.getReviewCount()), sv.getThumbnail()
                )).toList();

        return new PagedStores(content, page, size, filtered.size());
    }

    // ---------- helpers ----------
    private QuickFilter parseFilter(String f) {
        try { return QuickFilter.valueOf(f.toUpperCase()); }
        catch (Exception e) { return QuickFilter.ALL; }
    }
    private SortType parseSort(String s) {
        try { return SortType.valueOf(s); }
        catch (Exception e) { return SortType.rating; }
    }

    private List<Market> filterByRadiusMarket(List<Market> src, Double lat, Double lon, int r) {
        if (lat == null || lon == null) return List.of();
        return src.stream()
                .filter(m -> m.getLat()!=null && m.getLon()!=null
                        && distanceMeters(lat, lon, m.getLat(), m.getLon()) <= r)
                .toList();
    }
    private List<ApiStore> filterByRadiusStore(List<ApiStore> src, Double lat, Double lon, int r) {
        if (lat == null || lon == null) return List.of();
        return src.stream()
                .filter(s -> s.getLat()!=null && s.getLon()!=null
                        && distanceMeters(lat, lon, s.getLat(), s.getLon()) <= r)
                .toList();
    }

    private static double distanceMeters(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1==null || lon1==null || lat2==null || lon2==null) return 1e15;
        double R=6371000.0, dLat=Math.toRadians(lat2-lat1), dLon=Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2)*Math.sin(dLon/2);
        return 2*R*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }
    private static Double nz(Double v){ return v!=null?v:0.0; }
    private static Integer nzi(Integer v){ return v!=null?v:0; }
    private static <T> List<T> slice(List<T> list,int page,int size){
        int from=Math.max(0,page*size), to=Math.min(list.size(), from+size);
        if(from>=to) return List.of(); return list.subList(from, to);
    }
}