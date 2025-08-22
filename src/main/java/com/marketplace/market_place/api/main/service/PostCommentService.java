package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.entity.Post;
import com.marketplace.market_place.api.main.entity.PostComment;
import com.marketplace.market_place.api.main.repository.PostCommentRepository;
import com.marketplace.market_place.api.main.repository.PostRepository;
import com.marketplace.market_place.domain.User;
import com.marketplace.market_place.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommentService {

    private final PostCommentRepository repository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 기본 CRUD (기존 유지)
    public List<PostComment> findAll() { return repository.findAll(); }
    public Optional<PostComment> findById(Long id) { return repository.findById(id); }
    public PostComment save(PostComment entity) { return repository.save(entity); }
    public void delete(Long id) { repository.deleteById(id); }

    // 특정 게시글의 댓글 조회
    @Transactional(readOnly = true)
    public List<PostComment> findByPostId(Long postId) {
        return repository.findByPostIdOrderByIdDesc(postId);
    }

    // 댓글 등록
    public PostComment createComment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);

        return repository.save(comment);
    }

    // 댓글 삭제 (본인만 가능)
    public boolean deleteComment(Long commentId, Long userId) {
        Optional<PostComment> commentOpt = repository.findById(commentId);
        if (commentOpt.isEmpty()) {
            return false;
        }

        PostComment comment = commentOpt.get();
        if (!comment.getUser().getId().equals(userId)) {
            return false; // 본인 댓글이 아님
        }

        repository.delete(comment);
        return true;
    }
}
