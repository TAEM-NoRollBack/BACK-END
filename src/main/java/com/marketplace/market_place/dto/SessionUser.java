package com.marketplace.market_place.dto;

import com.marketplace.market_place.domain.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private Long id; // id 필드 추가
    private String name;
    private String email;
    private String provider;
    private String providerId;

    public SessionUser(User user) {
        this.id = user.getId(); // 생성자에서 id 초기화
        this.name = user.getName();
        this.email = user.getEmail();
        this.provider = user.getProvider();
        this.providerId = user.getProviderId();
    }
}
