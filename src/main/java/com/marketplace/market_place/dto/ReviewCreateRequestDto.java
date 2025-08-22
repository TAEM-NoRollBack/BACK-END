package com.marketplace.market_place.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter // @RequestPart에서 객체 매핑을 위해 Setter가 필요할 수 있습니다.
public class ReviewCreateRequestDto {
    private String content;
    private int rating;
}
