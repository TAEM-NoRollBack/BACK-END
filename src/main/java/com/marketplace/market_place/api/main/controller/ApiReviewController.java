package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.entity.ApiReview;
import com.marketplace.market_place.api.main.service.ApiReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ApiReviewController {

    private final ApiReviewService service;

    @GetMapping
    public List<ApiReview> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<ApiReview> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApiReview create(@RequestBody ApiReview entity) { return service.save(entity); }

    @PutMapping("/{id}")
    public ResponseEntity<ApiReview> update(@PathVariable Long id, @RequestBody ApiReview entity) {
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
