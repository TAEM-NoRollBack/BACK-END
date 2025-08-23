package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.entity.ApiBookmark;
import com.marketplace.market_place.api.main.repository.ApiBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiBookmarkService {

    private final ApiBookmarkRepository repository;

    public List<ApiBookmark> findAll() { return repository.findAll(); }

    public Optional<ApiBookmark> findById(Long id) { return repository.findById(id); }

    public ApiBookmark save(ApiBookmark entity) { return repository.save(entity); }

    public void delete(Long id) { repository.deleteById(id); }
}
