package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.entity.Market;
import com.marketplace.market_place.api.main.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/markets")
public class MarketController {

    private final MarketService service;

    @GetMapping
    public List<Market> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Market> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Market create(@RequestBody Market entity) { return service.save(entity); }

    @PutMapping("/{id}")
    public ResponseEntity<Market> update(@PathVariable Long id, @RequestBody Market entity) {
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
