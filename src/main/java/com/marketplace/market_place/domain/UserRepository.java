package com.marketplace.market_place.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // 기본 CRUD 메서드 제공됨: findById, save, delete 등
}
