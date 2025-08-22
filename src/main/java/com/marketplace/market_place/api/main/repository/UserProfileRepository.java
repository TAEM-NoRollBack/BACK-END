package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
