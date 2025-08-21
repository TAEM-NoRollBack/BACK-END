package com.marketplace.market_place.repository;

import com.marketplace.market_place.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    /**
     * 카카오맵의 장소 ID로 가게 정보를 조회합니다.
     * @param kakaoPlaceId 카카오맵 장소 ID
     * @return Optional<Store>
     */
    Optional<Store> findByKakaoPlaceId(Long kakaoPlaceId);
}
