package com.marketplace.market_place.api.main.service;

import com.marketplace.market_place.api.main.dto.UserProfileUpdateRequest;
import com.marketplace.market_place.api.main.entity.UserProfile;
import com.marketplace.market_place.api.main.repository.*;
import com.marketplace.market_place.domain.User;
import com.marketplace.market_place.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    private final BookmarkRepository bookmarkRepository;
    private final RewardRepository rewardRepository;

    // 프로필 조회
    @Transactional(readOnly = true)
    public Map<String, Object> getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Optional<UserProfile> profile = userProfileRepository.findById(userId);

        return Map.of(
                "userId", userId,
                "name", user.getName(),
                "email", user.getEmail() != null ? user.getEmail() : "",
                "nickname", profile.map(UserProfile::getNickname).orElse(user.getName()),
                "profileImageUrl", profile.map(UserProfile::getProfileImageUrl).orElse("/default-profile.jpg")
        );
    }

    // 프로필 수정
    public void updateUserProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserProfile profile = userProfileRepository.findById(userId)
                .orElse(new UserProfile());

        if (profile.getUserId() == null) {
            profile.setUserId(userId);
            profile.setUser(user);
        }

        if (request.getNickname() != null) {
            profile.setNickname(request.getNickname());
        }
        if (request.getProfileImageUrl() != null) {
            profile.setProfileImageUrl(request.getProfileImageUrl());
        }

        userProfileRepository.save(profile);
    }

    // 회원 탈퇴
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // 활동 내역 조회
    @Transactional(readOnly = true)
    public Map<String, Object> getUserActivity(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. 내가 쓴 게시글
        var posts = postRepository.findByUserOrderByCreatedAtDesc(user);

        // 2. 내가 쓴 리뷰
        var reviews = reviewRepository.findByUserOrderByIdDesc(user);

        // 3. 북마크한 상점
        var bookmarks = bookmarkRepository.findByUserOrderByIdDesc(user);

        // 4. 적립 리워드
        var rewards = rewardRepository.findByUserOrderByIdDesc(user);
        int totalPoints = rewards.stream().mapToInt(r -> r.getPoints()).sum();

        return Map.of(
                "success", true,
                "message", "활동 내역 조회 성공",
                "data", Map.of(
                        "posts", posts,
                        "reviews", reviews,
                        "bookmarks", bookmarks,
                        "totalRewardPoints", totalPoints,
                        "rewardHistory", rewards
                )
        );
    }
}
