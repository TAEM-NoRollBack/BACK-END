package com.marketplace.market_place.controller;

import com.marketplace.market_place.domain.Store;
import com.marketplace.market_place.dto.SessionUser;
import com.marketplace.market_place.dto.StoreDetailResponseDto;
import com.marketplace.market_place.service.BookmarkService;
import com.marketplace.market_place.service.StoreService;
import com.marketplace.market_place.service.KakaoApiService; // New import
import com.marketplace.market_place.dto.KakaoApiResponseDto; // New import
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreController {

    private final StoreService storeService;
    private final BookmarkService bookmarkService;
    private final KakaoApiService kakaoApiService; // New injection

    @GetMapping("/details")
    public ResponseEntity<StoreDetailResponseDto> getStoreDetails(
            @RequestParam("id") Long kakaoPlaceId,
            @RequestParam("name") String placeName) {

        // Use the new service method to get store details with bookmark status
        StoreDetailResponseDto responseDto = storeService.getStoreDetailsWithBookmarkStatus(kakaoPlaceId, placeName);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/search") // New endpoint
    public ResponseEntity<KakaoApiResponseDto> searchStoresByKeyword(@RequestParam("keyword") String keyword) {
        KakaoApiResponseDto response = kakaoApiService.requestStoreInfoByKeyword(keyword);
        if (response != null && response.getDocuments() != null && !response.getDocuments().isEmpty()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{storeId}/bookmarks")
    public ResponseEntity<Void> addBookmark(@PathVariable Long storeId, HttpSession httpSession) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        bookmarkService.addBookmark(user.getId(), storeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{storeId}/bookmarks")
    public ResponseEntity<Void> removeBookmark(@PathVariable Long storeId, HttpSession httpSession) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        bookmarkService.removeBookmark(user.getId(), storeId);
        return ResponseEntity.noContent().build();
    }
}
