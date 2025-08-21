package com.marketplace.market_place.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoApiResponseDto {

    @JsonProperty("documents")
    private List<Document> documents;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {

        @JsonProperty("id")
        private Long kakaoPlaceId;

        @JsonProperty("place_name")
        private String name;

        @JsonProperty("address_name")
        private String address;

        @JsonProperty("phone")
        private String phoneNumber;

        // 필요한 다른 필드가 있다면 여기에 추가할 수 있습니다.
        // 예: @JsonProperty("road_address_name") private String roadAddress;
    }
}
