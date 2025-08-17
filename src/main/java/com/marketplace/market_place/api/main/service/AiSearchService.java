package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.dto.AiSearchRequest;
import com.marketplace.market_place.api.main.dto.AiSearchResponse;
import com.marketplace.market_place.api.main.dto.AiStoreItem;
import com.marketplace.market_place.api.main.entity.Store;
import com.marketplace.market_place.api.main.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AiSearchService {

    private final StoreRepository storeRepository;
    private final AiSearchLLM ai;

    // ---- Helpers to reduce warnings & improve type-safety ----
    private static final class ScoredStore {
        final double score;
        final Double dist; // may be null
        final Store store;

        ScoredStore(double score, Double dist, Store store) {
            this.score = score;
            this.dist = dist;
            this.store = store;
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> asStringList(Object obj) {
        if (obj instanceof List<?> list) {
            List<String> out = new ArrayList<>(list.size());
            for (Object o : list) {
                if (o != null) out.add(o.toString());
            }
            return out;
        }
        return List.of();
    }

    @Transactional(readOnly = true)
    public AiSearchResponse search(AiSearchRequest req) {
        String prompt = req.getPrompt()==null? "" : req.getPrompt().trim();
        if (prompt.isEmpty()) throw new IllegalArgumentException("prompt는 필수입니다.");

        Double lat = req.getLat();
        Double lon = req.getLon();
        int baseRadius = req.getRadius()!=null ? req.getRadius() : 2500;
        int limit  = req.getLimit()!=null  ? Math.min(50, Math.max(1, req.getLimit())) : 10;

        // 1) LLM으로 조건 추출(키 없으면 내부 fallback)
        Map<String, Object> f = ai.extractFilters(prompt, lat, lon);
        List<String> keywords = asStringList(f.get("keywords"));
        List<String> cuisine  = asStringList(f.get("cuisine"));
        List<String> vibe     = asStringList(f.get("vibe"));
        boolean nearby = Boolean.TRUE.equals(f.get("nearby"));
        Integer rFromLLM = (Integer) f.get("radius");
        final int finalRadius = (rFromLLM != null) ? rFromLLM : baseRadius;

        // 2) 후보 필터링
        List<Store> all = storeRepository.findAll();
        List<Store> filtered = all.stream().filter(s -> {
            String bundle = ((s.getName()==null?"":s.getName()) + " " + (s.getDescription()==null?"":s.getDescription())).toLowerCase();
            boolean ok = true;
            for (String k: keywords) ok &= bundle.contains(k.toLowerCase());
            for (String c: cuisine)  ok &= bundle.contains(c.toLowerCase());
            for (String v: vibe)     ok &= bundle.contains(v.toLowerCase());
            if (nearby && lat!=null && lon!=null) {
                ok &= s.getLat()!=null && s.getLon()!=null && distanceMeters(lat,lon,s.getLat(),s.getLon()) <= finalRadius;
            }
            return ok;
        }).toList();

        // 3) 점수(관련도 + 평점 + 리뷰) → 정렬
        List<ScoredStore> scored = filtered.stream()
                .map(s -> {
                    double rel = relevance(keywords, s.getName(), s.getDescription());
                    double rate = nz(s.getRating());
                    double rev  = Math.log1p(nzi(s.getReviewCount()));
                    double score = 0.6 * rel + 0.25 * rate + 0.15 * rev;
                    Double dist = (lat != null && lon != null && s.getLat() != null && s.getLon() != null)
                            ? distanceMeters(lat, lon, s.getLat(), s.getLon()) : null;
                    return new ScoredStore(score, dist, s);
                })
                .sorted(Comparator
                        .comparingDouble((ScoredStore ss) -> -ss.score)
                        .thenComparing(ss -> ss.dist == null ? 1e15 : ss.dist))
                .limit(limit)
                .toList();

        List<AiStoreItem> items = scored.stream().map(ss -> {
            Store s = ss.store;
            Double dist = ss.dist;
            String reason = buildReason(cuisine, vibe, s);
            return new AiStoreItem(
                    s.getId(), s.getName(),
                    s.getMarket() != null ? s.getMarket().getName() : null,
                    s.getLat(), s.getLon(),
                    nz(s.getRating()), nzi(s.getReviewCount()), s.getThumbnail(),
                    dist, reason
            );
        }).toList();

        // 4) 요약(키 없으면 서버 기본 문장)
        String summary = ai.summarize(prompt, filtered.size(), lat, lon);

        return new AiSearchResponse(summary, items);
    }

    private static double relevance(List<String> keywords, String name, String desc){
        String bundle = ((name==null?"":name)+" "+(desc==null?"":desc)).toLowerCase();
        double score = 0.0;
        for (String k: keywords) {
            String q = k.toLowerCase();
            if (name!=null && name.toLowerCase().startsWith(q)) score += 3.0;
            else if (bundle.contains(q)) score += 1.0;
        }
        return score;
    }
    private static String buildReason(List<String> cuisine, List<String> vibe, Store s){
        String base = "평점 " + nz(s.getRating()) + ", 리뷰 " + nzi(s.getReviewCount());
        if (!cuisine.isEmpty()) base = String.join("/", cuisine) + " 연관, " + base;
        if (!vibe.isEmpty())    base = String.join("/", vibe) + " 분위기, " + base;
        return base;
    }

    private static double distanceMeters(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1==null || lon1==null || lat2==null || lon2==null) return 1e15;
        double R=6371000.0, dLat=Math.toRadians(lat2-lat1), dLon=Math.toRadians(lon2-lon1);
        double a=Math.sin(dLat/2)*Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2)*Math.sin(dLon/2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
    private static Double nz(Double v){ return v!=null?v:0.0; }
    private static Integer nzi(Integer v){ return v!=null?v:0; }
}