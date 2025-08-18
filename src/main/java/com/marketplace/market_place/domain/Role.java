package com.marketplace.market_place.domain;

public enum Role {
    USER,
    ADMIN;

    public String getKey() {
        return this.name();
    }
}
