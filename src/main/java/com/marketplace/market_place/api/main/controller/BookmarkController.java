package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.entity.Bookmark;
import com.marketplace.market_place.api.main.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmarks")
public class BookmarkController {

    private final BookmarkService service;

    @GetMapping
    public List<Bookmark> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Bookmark> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Bookmark create(@RequestBody Bookmark entity) { return service.save(entity); }

    @PutMapping("/{id}")
    public ResponseEntity<Bookmark> update(@PathVariable Long id, @RequestBody Bookmark entity) {
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
