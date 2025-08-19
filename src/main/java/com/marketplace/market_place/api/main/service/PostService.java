package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.entity.Post;
import com.marketplace.market_place.api.main.entity.Reward;
import com.marketplace.market_place.api.main.repository.PostRepository;
import com.marketplace.market_place.api.main.repository.RewardRepository;
import com.marketplace.market_place.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final RewardRepository rewardRepository;
    private final PostImageService postImageService; // 이미지 저장 연동

    // 글 작성 (리워드 지급 포함) - 텍스트만
    public Post savePost(Post post) {
        Post savedPost = postRepository.save(post);

        // 임시저장이 아닌 경우에만 리워드 지급
        if (!Boolean.TRUE.equals(post.getIsDraft())) {
            giveRewardForPost(post.getUser());
        }

        // 컬렉션 초기화(응답 직렬화 시 지연로딩 문제 방지용)
        savedPost.getImages().size();
        savedPost.getLikes().size();
        savedPost.getComments().size();

        return savedPost;
    }

    // 글 작성 (리워드 + 이미지 업로드까지 한 번에)
    public Post savePostWithImages(Post post, List<MultipartFile> files, User user) {
        post.setUser(user);
        Post savedPost = postRepository.save(post);

        // 임시저장이 아닌 경우에만 리워드 지급
        if (!Boolean.TRUE.equals(post.getIsDraft())) {
            giveRewardForPost(user);
        }

        // 이미지가 있다면 업로드
        if (files != null && !files.isEmpty()) {
            var uploaded = postImageService.uploadImages(savedPost.getId(), files, user.getId());
            // 응답에서 이미지 보이도록 컬렉션 동기화/초기화
            savedPost.getImages().clear();
            savedPost.getImages().addAll(uploaded);
        }

        // 컬렉션 초기화
        savedPost.getImages().size();
        savedPost.getLikes().size();
        savedPost.getComments().size();

        return savedPost;
    }

    // 리워드 지급 (+200 포인트)
    private void giveRewardForPost(User user) {
        Reward reward = new Reward();
        reward.setUser(user);
        reward.setPoints(200);
        rewardRepository.save(reward);
    }

    // 글 조회 (공개글 + 작성자 본인 비공개글 포함)
    @Transactional(readOnly = true)
    public Optional<Post> findByIdWithAccessCheck(Long postId, User user) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return Optional.empty();
        }

        Post post = postOpt.get();
        if (Boolean.TRUE.equals(post.getIsPublic())) {
            return Optional.of(post);
        } else {
            if (user != null && post.getUser().getId().equals(user.getId())) {
                return Optional.of(post);
            } else {
                return Optional.empty();
            }
        }
    }

    // 본인 글만 수정 가능 체크 + 글 수정
    public Optional<Post> updatePost(Long postId, Post updatedPost, User user) {
        Optional<Post> postOpt = postRepository.findById(postId);

        if (postOpt.isEmpty()) return Optional.empty();

        Post post = postOpt.get();
        if (!post.getUser().getId().equals(user.getId())) {
            return Optional.empty();
        }

        post.setTitle(updatedPost.getTitle());
        post.setLocation(updatedPost.getLocation());
        post.setRating(updatedPost.getRating());
        post.setContent(updatedPost.getContent());
        post.setIsDraft(updatedPost.getIsDraft());
        post.setIsPublic(updatedPost.getIsPublic());

        return Optional.of(post);
    }

    // 본인 글만 삭제 가능
    public boolean deletePost(Long postId, User user) {
        Optional<Post> postOpt = postRepository.findById(postId);

        if (postOpt.isEmpty()) return false;

        Post post = postOpt.get();
        if (!post.getUser().getId().equals(user.getId())) {
            return false;
        }

        postRepository.delete(post);
        return true;
    }

    // 임시저장 리스트 조회 (본인)
    @Transactional(readOnly = true)
    public List<Post> findDraftsByUser(User user) {
        return postRepository.findByUserAndIsDraft(user, true);
    }

    // 공개글 리스트 조회
    @Transactional(readOnly = true)
    public List<Post> findPublicPosts() {
        return postRepository.findByIsPublicOrderByCreatedAtDesc(true);
    }
}
