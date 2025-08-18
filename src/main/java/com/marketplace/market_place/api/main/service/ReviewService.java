package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.entity.Review;
import com.marketplace.market_place.api.main.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository repository;

    public List<Review> findAll() { return repository.findAll(); }

    public Optional<Review> findById(Long id) { return repository.findById(id); }

    public Review save(Review entity) { return repository.save(entity); }

    public void delete(Long id) { repository.deleteById(id); }
}
