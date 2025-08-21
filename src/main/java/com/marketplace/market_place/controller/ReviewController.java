package com.marketplace.market_place.controller;

import com.marketplace.market_place.dto.ReviewCreateRequestDto;
import com.marketplace.market_place.dto.ReviewResponseDto;
import com.marketplace.market_place.dto.SessionUser;
import com.marketplace.market_place.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.market_place.dto.ReviewUpdateRequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/stores/{storeId}/reviews", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createReview(
            @PathVariable Long storeId,
            @RequestPart("review") String reviewJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpSession httpSession) {

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            ReviewCreateRequestDto requestDto = objectMapper.readValue(reviewJson, ReviewCreateRequestDto.class);

            ReviewResponseDto responseDto = reviewService.createReview(user.getId(), storeId, requestDto, images);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 생성 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PutMapping(value = "/reviews/{reviewId}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @RequestPart("review") String reviewJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpSession httpSession) {

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            ReviewUpdateRequestDto requestDto = objectMapper.readValue(reviewJson, ReviewUpdateRequestDto.class);

            ReviewResponseDto responseDto = reviewService.updateReview(user.getId(), reviewId, requestDto, images);
            return ResponseEntity.ok(responseDto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 수정 중 오류가 발생했습니다: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/stores/{storeId}/reviews") // New endpoint
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByStoreId(@PathVariable Long storeId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByStoreId(storeId);
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, HttpSession httpSession) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            reviewService.deleteReview(user.getId(), reviewId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 삭제 중 파일 시스템 오류가 발생했습니다: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류가 발생했습니다: " + e.getMessage());
        }
    }
}