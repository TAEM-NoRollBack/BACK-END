package com.marketplace.market_place.service;

import com.marketplace.market_place.domain.User;
import com.marketplace.market_place.dto.SessionUser;
import com.marketplace.market_place.dto.UserSignUpRequestDto;
import com.marketplace.market_place.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User signUp(UserSignUpRequestDto requestDto, SessionUser sessionUser) {
        if (sessionUser == null) {
            throw new IllegalArgumentException("User not authenticated.");
        }

        User user = userRepository.findByProviderAndProviderId(sessionUser.getProvider(), sessionUser.getProviderId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user session"));

        // Update user with additional info and change role to USER
        user.updateAdditionalInfo(
                requestDto.getName(),
                requestDto.getNickname(),
                requestDto.getEmail(), // DTO에서 받은 이메일 사용
                requestDto.getGender(),
                requestDto.getUniversity(),
                requestDto.getDepartment(),
                requestDto.getBirthdate()
        );

        return user;
    }
}
