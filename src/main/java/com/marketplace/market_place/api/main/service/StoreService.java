package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.entity.Store;
import com.marketplace.market_place.api.main.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository repository;

    public List<Store> findAll() { return repository.findAll(); }

    public Optional<Store> findById(Long id) { return repository.findById(id); }

    public Store save(Store entity) { return repository.save(entity); }

    public void delete(Long id) { repository.deleteById(id); }
}
