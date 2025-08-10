package com.marketplace.market_place.controller;

import com.marketplace.market_place.dto.SessionUser;
import com.marketplace.market_place.dto.UserSignUpRequestDto;
import com.marketplace.market_place.domain.User;
import com.marketplace.market_place.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final HttpSession httpSession;

    @PostMapping("/api/v1/signup")
    public ResponseEntity<String> signUp(@RequestBody UserSignUpRequestDto requestDto) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (sessionUser == null) {
            return ResponseEntity.status(401).body("User not authenticated.");
        }

        User updatedUser = userService.signUp(requestDto, sessionUser);

        // Update the session with the new user info (now with Role.USER)
        httpSession.setAttribute("user", new SessionUser(updatedUser));

        return ResponseEntity.ok("User registration successful.");
    }
}
