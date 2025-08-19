package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.entity.Post;
import com.marketplace.market_place.api.main.entity.PostImage;
import com.marketplace.market_place.api.main.repository.PostImageRepository;
import com.marketplace.market_place.api.main.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;

    @Value("${app.upload.dir:/uploads/posts}")
    private String uploadDir;

    @Value("${app.upload.url:/uploads/posts}")
    private String uploadUrl;

    // 이미지 업로드
    public List<PostImage> uploadImages(Long postId, List<MultipartFile> files, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 권한 체크 (본인 게시글만 이미지 업로드 가능)
        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        List<PostImage> uploadedImages = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            if (file.isEmpty()) continue;

            try {
                String savedImageUrl = saveFile(file);

                PostImage image = PostImage.builder()
                        .post(post)
                        .imageUrl(savedImageUrl)
                        .originalFilename(file.getOriginalFilename())
                        .fileSize(file.getSize())
                        .displayOrder(i + 1)
                        .build();

                PostImage savedImage = postImageRepository.save(image);
                uploadedImages.add(savedImage);

            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패: " + file.getOriginalFilename(), e);
            }
        }

        return uploadedImages;
    }

    // 파일 저장
    private String saveFile(MultipartFile file) throws IOException {
        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 고유 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // 파일 저장
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        // 웹에서 접근 가능한 URL 반환
        return uploadUrl + "/" + uniqueFilename;
    }

    // 게시글의 이미지 목록 조회
    @Transactional(readOnly = true)
    public List<PostImage> getImagesByPostId(Long postId) {
        return postImageRepository.findByPostIdOrderByDisplayOrder(postId);
    }

    // 이미지 삭제
    public boolean deleteImage(Long imageId, Long userId) {
        Optional<PostImage> imageOpt = postImageRepository.findById(imageId);
        if (imageOpt.isEmpty()) {
            return false;
        }

        PostImage image = imageOpt.get();

        // 권한 체크 (게시글 작성자만 삭제 가능)
        if (!image.getPost().getUser().getId().equals(userId)) {
            return false;
        }

        try {
            // 실제 파일 삭제
            deleteFile(image.getImageUrl());
            // DB에서 삭제
            postImageRepository.delete(image);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 실제 파일 삭제
    private void deleteFile(String imageUrl) {
        try {
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir, filename);
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            // 파일 삭제 실패해도 DB는 삭제되도록 함
        }
    }
}
