package com.marketplace.market_place.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "bookmarks",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "bookmark_uk",
            columnNames = {"user_id", "store_id"}
        )
    }
)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 북마크한 유저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store; // 북마크된 가게

    @Builder
    public Bookmark(User user, Store store) {
        this.user = user;
        this.store = store;
    }
}
