package com.marketplace.market_place.service;

import com.marketplace.market_place.dto.KakaoApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoApiService {

    private final WebClient.Builder webClientBuilder;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private static final String KAKAO_API_URL = "https://dapi.kakao.com";
    private static final String SEARCH_ADDRESS_URI = "/v2/local/search/address.json";
    private static final String SEARCH_KEYWORD_URI = "/v2/local/search/keyword.json";

    /**
     * 키워드로 장소 정보를 검색합니다.
     * @param keyword 검색할 키워드 (가게 이름 등)
     * @return KakaoApiResponseDto
     */
    public KakaoApiResponseDto requestStoreInfoByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        WebClient webClient = webClientBuilder.baseUrl(KAKAO_API_URL).build();

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(SEARCH_KEYWORD_URI)
                            .queryParam("query", keyword)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoApiKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(KakaoApiResponseDto.class)
                    .block(); // Blocking for simplicity, consider async in production
        } catch (WebClientResponseException e) {
            log.error("Error during Kakao API call (WebClientResponseException): status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            return null; // Or throw a custom exception
        } catch (Exception e) {
            log.error("Unexpected error during Kakao API call: {}", e.getMessage(), e);
            return null; // Or throw a custom exception
        }
    }
}
