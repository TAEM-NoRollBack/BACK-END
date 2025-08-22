package com.marketplace.market_place.api.main.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor      // JPA 기본 생성자
@Entity
@Table(name = "market")
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 기본 정보
    @Column(nullable = false)
    private String name;

    private String address;

    // 좌표
    private Double lat;     // 위도
    private Double lon;     // 경도

    // 전시용 지표(메인 카드에 필요)
    private Double rating;      // 평균 평점
    private Integer reviewCount;// 리뷰 개수
    private String thumbnail;   // 대표 이미지 URL
}