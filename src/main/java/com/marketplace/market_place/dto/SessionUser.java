package com.marketplace.market_place.dto;

import com.marketplace.market_place.domain.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String provider;
    private String providerId;

    public SessionUser(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.provider = user.getProvider();
        this.providerId = user.getProviderId();
    }
}
