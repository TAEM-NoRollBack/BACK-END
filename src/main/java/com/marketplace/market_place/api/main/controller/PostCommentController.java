package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.entity.PostComment;
import com.marketplace.market_place.api.main.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post-comments")
public class PostCommentController {

    private final PostCommentService service;

    @GetMapping
    public List<PostComment> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<PostComment> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PostComment create(@RequestBody PostComment entity) { return service.save(entity); }

    @PutMapping("/{id}")
    public ResponseEntity<PostComment> update(@PathVariable Long id, @RequestBody PostComment entity) {
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
