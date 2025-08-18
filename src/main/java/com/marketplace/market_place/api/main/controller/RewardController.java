package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.entity.Reward;
import com.marketplace.market_place.api.main.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rewards")
public class RewardController {

    private final RewardService service;

    @GetMapping
    public List<Reward> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Reward> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Reward create(@RequestBody Reward entity) { return service.save(entity); }

    @PutMapping("/{id}")
    public ResponseEntity<Reward> update(@PathVariable Long id, @RequestBody Reward entity) {
        return service.findById(id).map(ex -> {
            entity.setId(id);
            return ResponseEntity.ok(service.save(entity));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
