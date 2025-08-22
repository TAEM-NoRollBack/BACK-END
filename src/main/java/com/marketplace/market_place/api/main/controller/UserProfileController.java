package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.dto.UserProfileUpdateRequest;
import com.marketplace.market_place.api.main.service.UserProfileService;
import com.marketplace.market_place.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class UserProfileController {

    private final UserProfileService userProfileService;

    // -----------------------------
    // 1️⃣ 프로필 조회
    // -----------------------------
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal User user) {
        Map<String, Object> data = userProfileService.getUserProfile(user.getId());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "프로필 조회 성공",
                "data", data
        ));
    }

    // -----------------------------
    // 2️⃣ 프로필 수정
    // -----------------------------
    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(
            @RequestBody UserProfileUpdateRequest request,
            @AuthenticationPrincipal User user
    ) {
        userProfileService.updateUserProfile(user.getId(), request);
        return ResponseEntity.ok().build();
    }

    // -----------------------------
    // 3️⃣ 회원 탈퇴
    // -----------------------------
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteUser(@AuthenticationPrincipal User user) {
        userProfileService.deleteUser(user.getId());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "회원 탈퇴 완료"
        ));
    }

    // -----------------------------
    // 4️⃣ 활동 내역 조회
    // -----------------------------
    @GetMapping("/activity")
    public ResponseEntity<Map<String, Object>> getActivity(@AuthenticationPrincipal User user) {
        Map<String, Object> activity = userProfileService.getUserActivity(user.getId());
        return ResponseEntity.ok(activity);
    }
}
