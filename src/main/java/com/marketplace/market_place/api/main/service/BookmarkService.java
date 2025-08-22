package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.entity.Bookmark;
import com.marketplace.market_place.api.main.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository repository;

    public List<Bookmark> findAll() { return repository.findAll(); }

    public Optional<Bookmark> findById(Long id) { return repository.findById(id); }

    public Bookmark save(Bookmark entity) { return repository.save(entity); }

    public void delete(Long id) { repository.deleteById(id); }
}
