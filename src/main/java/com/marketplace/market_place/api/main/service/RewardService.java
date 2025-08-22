package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.entity.Reward;
import com.marketplace.market_place.api.main.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository repository;

    public List<Reward> findAll() { return repository.findAll(); }

    public Optional<Reward> findById(Long id) { return repository.findById(id); }

    public Reward save(Reward entity) { return repository.save(entity); }

    public void delete(Long id) { repository.deleteById(id); }
}
