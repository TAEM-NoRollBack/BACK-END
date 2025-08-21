package com.marketplace.market_place.service;

import com.marketplace.market_place.domain.Review;
import com.marketplace.market_place.domain.ReviewImage;
import com.marketplace.market_place.domain.Store;
import com.marketplace.market_place.domain.User;
import com.marketplace.market_place.dto.ReviewCreateRequestDto;
import com.marketplace.market_place.dto.ReviewResponseDto;
import com.marketplace.market_place.dto.ReviewUpdateRequestDto;
import com.marketplace.market_place.repository.ReviewImageRepository;
import com.marketplace.market_place.repository.ReviewRepository;
import com.marketplace.market_place.repository.StoreRepository;
import com.marketplace.market_place.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors; // New import

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ReviewImageRepository reviewImageRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ReviewResponseDto createReview(Long userId, Long storeId, ReviewCreateRequestDto requestDto, List<MultipartFile> images) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. id=" + userId));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다. id=" + storeId));

        // 1. 리뷰 텍스트 정보 저장
        Review review = Review.builder()
                .content(requestDto.getContent())
                .rating(requestDto.getRating())
                .user(user)
                .store(store)
                .build();

        // 2. 이미지 파일 저장 및 ReviewImage 생성
        if (images != null && !images.isEmpty()) {
            // 업로드 디렉토리가 없으면 생성
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile imageFile : images) {
                if (!imageFile.isEmpty()) {
                    String originalFileName = imageFile.getOriginalFilename();
                    String storedFileName = createStoredFileName(originalFileName);

                    // 파일을 서버에 저장
                    imageFile.transferTo(new File(uploadDir + storedFileName));

                    // ReviewImage 객체 생성 및 Review에 추가
                    ReviewImage reviewImage = ReviewImage.builder()
                            .imageUrl(storedFileName)
                            .build();
                    review.addImage(reviewImage);
                }
            }
        }

        Review savedReview = reviewRepository.save(review);
        return ReviewResponseDto.from(savedReview);
    }

    // 서버 내부에서 관리할 고유한 파일명 생성
    private String createStoredFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 확장자 추출
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    @Transactional
    public ReviewResponseDto updateReview(Long userId, Long reviewId, ReviewUpdateRequestDto requestDto, List<MultipartFile> newImages) throws IOException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 없습니다. id=" + reviewId));

        // Verify ownership
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("리뷰를 수정할 권한이 없습니다.");
        }

        // Update content and rating
        review.setContent(requestDto.getContent());
        review.setRating(requestDto.getRating());

        // Handle image updates
        // For simplicity, this example replaces all old images with new ones.
        // A more sophisticated approach would allow adding/deleting specific images.
        if (newImages != null && !newImages.isEmpty()) {
            // Delete old images from file system and database
            List<ReviewImage> oldImages = review.getImages(); // Assuming Review has getImages method
            if (oldImages != null) {
                for (ReviewImage oldImage : oldImages) {
                    Path oldFilePath = Paths.get(uploadDir + oldImage.getImageUrl());
                    Files.deleteIfExists(oldFilePath);
                    reviewImageRepository.delete(oldImage); // Delete from DB
                }
                review.clearImages(); // Assuming Review has clearImages method
            }

            // Save new images
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile imageFile : newImages) {
                if (!imageFile.isEmpty()) {
                    String originalFileName = imageFile.getOriginalFilename();
                    String storedFileName = createStoredFileName(originalFileName);

                    imageFile.transferTo(new File(uploadDir + storedFileName));

                    ReviewImage reviewImage = ReviewImage.builder()
                            .imageUrl(storedFileName)
                            .build();
                    review.addImage(reviewImage);
                }
            }
        } else {
            // If no new images are provided, but content/rating are updated,
            // and there were existing images, we might want to keep them.
            // This logic depends on desired behavior. For now, if newImages is null/empty,
            // existing images are kept.
        }

        Review updatedReview = reviewRepository.save(review);
        return ReviewResponseDto.from(updatedReview);
    }

    @Transactional(readOnly = true) // Read-only transaction for performance
    public List<ReviewResponseDto> getReviewsByStoreId(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다. id=" + storeId));

        List<Review> reviews = reviewRepository.findByStore(store);
        return reviews.stream()
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) throws IOException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 없습니다. id=" + reviewId));

        // Verify ownership
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("리뷰를 삭제할 권한이 없습니다.");
        }

        // Delete image files from file system
        List<ReviewImage> images = review.getImages();
        if (images != null && !images.isEmpty()) {
            for (ReviewImage image : images) {
                Path filePath = Paths.get(uploadDir + image.getImageUrl());
                Files.deleteIfExists(filePath);
            }
        }

        // The cascade setting will automatically delete ReviewImage entities from the database.
        reviewRepository.delete(review);
    }
}
