package com.marketplace.market_place.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long kakaoPlaceId; // 카카오맵에서 사용하는 장소의 고유 ID

    @Column(nullable = false)
    private String name; // 가게 이름

    @Column
    private String address; // 주소

    @Column
    private String phoneNumber; // 전화번호

    // Store가 사라지면 관련 Review도 모두 삭제되도록 Cascade 설정
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    // Store가 사라지면 관련 Bookmark도 모두 삭제되도록 Cascade 설정
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Builder
    public Store(Long kakaoPlaceId, String name, String address, String phoneNumber) {
        this.kakaoPlaceId = kakaoPlaceId;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
