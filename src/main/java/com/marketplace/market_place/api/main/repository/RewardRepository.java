package com.marketplace.market_place.api.main.repository;

import com.marketplace.market_place.api.main.entity.Reward;
import com.marketplace.market_place.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByUserOrderByIdDesc(User user);
}
