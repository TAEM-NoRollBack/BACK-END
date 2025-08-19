package com.marketplace.market_place.api.main.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateRequest {
    private String nickname;
    private String profileImageUrl;
}
