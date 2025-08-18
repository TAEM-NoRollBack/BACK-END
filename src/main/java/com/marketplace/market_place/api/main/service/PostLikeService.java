package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.entity.PostLike;
import com.marketplace.market_place.api.main.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository repository;

    public List<PostLike> findAll() { return repository.findAll(); }

    public Optional<PostLike> findById(Long id) { return repository.findById(id); }

    public PostLike save(PostLike entity) { return repository.save(entity); }

    public void delete(Long id) { repository.deleteById(id); }
}
