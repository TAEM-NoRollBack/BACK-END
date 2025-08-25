package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.entity.ApiReview;
import com.marketplace.market_place.api.main.repository.ApiReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiReviewService {

    private final ApiReviewRepository repository;

    public List<ApiReview> findAll() { return repository.findAll(); }

    public Optional<ApiReview> findById(Long id) { return repository.findById(id); }

    public ApiReview save(ApiReview entity) { return repository.save(entity); }

    public void delete(Long id) { repository.deleteById(id); }
}
