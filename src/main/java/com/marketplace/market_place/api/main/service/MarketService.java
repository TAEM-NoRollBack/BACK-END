package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.entity.Market;
import com.marketplace.market_place.api.main.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketService {

    private final MarketRepository repository;

    public List<Market> findAll() { return repository.findAll(); }

    public Optional<Market> findById(Long id) { return repository.findById(id); }

    public Market save(Market entity) { return repository.save(entity); }

    public void delete(Long id) { repository.deleteById(id); }
}
