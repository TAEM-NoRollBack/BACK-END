package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.entity.ApiBookmark;
import com.marketplace.market_place.api.main.service.ApiBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmarks")
public class BookmarkController {

    private final ApiBookmarkService service;

    @GetMapping
    public List<ApiBookmark> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<ApiBookmark> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApiBookmark create(@RequestBody ApiBookmark entity) { return service.save(entity); }

    @PutMapping("/{id}")
    public ResponseEntity<ApiBookmark> update(@PathVariable Long id, @RequestBody ApiBookmark entity) {
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
