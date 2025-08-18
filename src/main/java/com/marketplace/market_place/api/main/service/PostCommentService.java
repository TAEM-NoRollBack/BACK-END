package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.entity.PostComment;
import com.marketplace.market_place.api.main.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentRepository repository;

    public List<PostComment> findAll() { return repository.findAll(); }

    public Optional<PostComment> findById(Long id) { return repository.findById(id); }

    public PostComment save(PostComment entity) { return repository.save(entity); }

    public void delete(Long id) { repository.deleteById(id); }
}
