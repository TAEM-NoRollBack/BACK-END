package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.dto.LikeToggleResponse;
import com.marketplace.market_place.api.main.entity.Post;
import com.marketplace.market_place.api.main.entity.PostLike;
import com.marketplace.market_place.api.main.repository.PostLikeRepository;
import com.marketplace.market_place.api.main.repository.PostRepository;
import com.marketplace.market_place.domain.User;
import com.marketplace.market_place.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostRepository postRepository;
    private final UserRepository userRepository; // domain 패키지의 UserRepository
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public LikeToggleResponse toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + userId));

        boolean liked;

        // 이미 눌렀는지 확인
        var existing = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (existing.isPresent()) {
            // 이미 있으면 -> 취소(삭제)
            postLikeRepository.delete(existing.get());
            liked = false;
        } else {
            // 없으면 -> 등록
            try {
                PostLike like = new PostLike();
                like.setPost(post);
                like.setUser(user);
                postLikeRepository.save(like);
                liked = true;
            } catch (DataIntegrityViolationException e) {
                // 동시 클릭 등으로 유니크 충돌 시: 이미 등록된 것으로 간주
                liked = true;
            }
        }

        long likeCount = postLikeRepository.countByPostId(postId);
        return new LikeToggleResponse(postId, userId, liked, likeCount);
    }

    @Transactional(readOnly = true)
    public long countLikes(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }
}
