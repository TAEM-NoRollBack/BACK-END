package com.marketplace.market_place.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewUpdateRequestDto {
    private String content;
    private int rating;
}
