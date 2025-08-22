package com.marketplace.market_place.api.main.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "store")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 시장 소속 가게인지 (다:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id", nullable = false)
    private Market market;

    // 기본 정보
    @Column(nullable = false)
    private String name;

    private String description; // 한 줄 소개(선택)

    // 좌표
    private Double lat;   // 위도
    private Double lon;   // 경도

    // 전시용 지표(카드에 필요)
    private Double rating;       // 평균 평점
    private Integer reviewCount; // 리뷰 개수
    private String thumbnail;    // 대표 이미지 URL
}