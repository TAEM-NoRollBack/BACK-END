package com.marketplace.market_place.service;

import com.marketplace.market_place.domain.Bookmark;
import com.marketplace.market_place.domain.Store;
import com.marketplace.market_place.domain.User;
import com.marketplace.market_place.repository.BookmarkRepository;
import com.marketplace.market_place.repository.StoreRepository;
import com.marketplace.market_place.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    /**
     * 북마크를 추가합니다.
     * @param userId 현재 로그인한 유저의 ID
     * @param storeId 북마크할 가게의 ID
     */
    public void addBookmark(Long userId, Long storeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. id=" + userId));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다. id=" + storeId));

        // 이미 북마크가 존재하면 아무 작업도 하지 않음
        if (bookmarkRepository.findByUserAndStore(user, store).isPresent()) {
            return;
        }

        Bookmark newBookmark = Bookmark.builder()
                .user(user)
                .store(store)
                .build();
        bookmarkRepository.save(newBookmark);
    }

    /**
     * 북마크를 삭제합니다.
     * @param userId 현재 로그인한 유저의 ID
     * @param storeId 북마크할 가게의 ID
     */
    public void removeBookmark(Long userId, Long storeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. id=" + userId));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다. id=" + storeId));

        // 북마크 정보를 찾아서 삭제
        bookmarkRepository.findByUserAndStore(user, store)
                .ifPresent(bookmarkRepository::delete);
    }

    /**
     * 특정 유저가 특정 가게를 북마크했는지 여부를 확인합니다.
     * @param userId 현재 로그인한 유저의 ID
     * @param storeId 확인할 가게의 ID
     * @return 북마크 여부
     */
    @Transactional(readOnly = true)
    public boolean isBookmarked(Long userId, Long storeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. id=" + userId));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다. id=" + storeId));

        return bookmarkRepository.findByUserAndStore(user, store).isPresent();
    }
}
