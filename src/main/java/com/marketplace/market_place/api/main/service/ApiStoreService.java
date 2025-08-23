package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.entity.ApiStore;
import com.marketplace.market_place.api.main.repository.ApiStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiStoreService {

    private final ApiStoreRepository repository;

    public List<ApiStore> findAll() { return repository.findAll(); }

    public Optional<ApiStore> findById(Long id) { return repository.findById(id); }

    public ApiStore save(ApiStore entity) { return repository.save(entity); }

    public void delete(Long id) { repository.deleteById(id); }
}
