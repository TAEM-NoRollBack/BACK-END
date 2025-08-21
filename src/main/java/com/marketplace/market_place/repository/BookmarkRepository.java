package com.marketplace.market_place.repository;

import com.marketplace.market_place.domain.Bookmark;
import com.marketplace.market_place.domain.Store;
import com.marketplace.market_place.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * 특정 유저와 가게에 해당하는 북마크 정보를 조회합니다.
     * @param user 유저
     * @param store 가게
     * @return Optional<Bookmark>
     */
    Optional<Bookmark> findByUserAndStore(User user, Store store);
}
