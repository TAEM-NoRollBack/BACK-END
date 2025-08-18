package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.entity.PostLike;
import com.marketplace.market_place.api.main.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post-likes")
public class PostLikeController {

    private final PostLikeService service;

    @GetMapping
    public List<PostLike> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<PostLike> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PostLike create(@RequestBody PostLike entity) { return service.save(entity); }

    @PutMapping("/{id}")
    public ResponseEntity<PostLike> update(@PathVariable Long id, @RequestBody PostLike entity) {
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
