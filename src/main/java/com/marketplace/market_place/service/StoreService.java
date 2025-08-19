package com.marketplace.market_place.service;

import com.marketplace.market_place.domain.Store;
import com.marketplace.market_place.dto.KakaoApiResponseDto;
import com.marketplace.market_place.repository.StoreRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import com.marketplace.market_place.dto.StoreDetailResponseDto;
import com.marketplace.market_place.service.BookmarkService;
import com.marketplace.market_place.dto.SessionUser;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final KakaoApiService kakaoApiService;
    private final BookmarkService bookmarkService;
    private final HttpSession httpSession;

    /**
     * 가게 정보를 조회하거나, 없는 경우 API로 조회 후 생성합니다.
     * @param kakaoPlaceId 카카오맵 장소 ID
     * @param placeName 장소 이름 (카카오 API 검색용)
     * @return 조회되거나 생성된 Store 엔티티
     */
    @Transactional
    public Store findOrCreateStore(Long kakaoPlaceId, String placeName) {
        // 1. 우리 DB에서 먼저 가게를 찾아본다.
        Optional<Store> storeOptional = storeRepository.findByKakaoPlaceId(kakaoPlaceId);

        if (storeOptional.isPresent()) {
            // 2. DB에 가게가 존재하면, 그 정보를 바로 반환한다.
            return storeOptional.get();
        } else {
            // 3. DB에 가게가 없으면, 카카오 API를 호출한다.
            KakaoApiResponseDto apiResponse = kakaoApiService.requestStoreInfoByKeyword(placeName);

            if (apiResponse == null || apiResponse.getDocuments() == null || apiResponse.getDocuments().isEmpty()) {
                // TODO: 예외 처리 - API 결과가 없는 경우
                throw new IllegalArgumentException("해당 장소에 대한 정보를 카카오 API에서 찾을 수 없습니다.");
            }

            // API 응답 결과에서 kakaoPlaceId가 일치하는 정보를 찾는다.
            KakaoApiResponseDto.Document storeDocument = apiResponse.getDocuments().stream()
                    .filter(doc -> kakaoPlaceId.equals(doc.getKakaoPlaceId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("카카오 API 응답에 해당 장소 ID가 없습니다."));

            // 4. API 결과로 받은 정보로 새로운 Store 객체를 만들어 DB에 저장한다.
            Store newStore = Store.builder()
                    .kakaoPlaceId(storeDocument.getKakaoPlaceId())
                    .name(storeDocument.getName())
                    .address(storeDocument.getAddress())
                    .phoneNumber(storeDocument.getPhoneNumber())
                    .build();

            return storeRepository.save(newStore);
        }
    }

    // New method to get store details with bookmark status
    @Transactional
    public StoreDetailResponseDto getStoreDetailsWithBookmarkStatus(Long kakaoPlaceId, String name) {
        Store store = findOrCreateStore(kakaoPlaceId, name); // Ensure store exists in our DB

        boolean isBookmarked = false;
        SessionUser currentUser = (SessionUser) httpSession.getAttribute("user");
        if (currentUser != null && currentUser.getId() != null) {
            isBookmarked = bookmarkService.isBookmarked(currentUser.getId(), store.getId());
        }

        return StoreDetailResponseDto.from(store, isBookmarked);
    }
}
