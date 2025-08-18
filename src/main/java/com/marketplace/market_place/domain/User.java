package com.marketplace.market_place.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String email;

    @Column
    private String nickname;

    @Column
    private String gender;

    @Column
    private String university;

    @Column
    private String department;

    @Column
    private String birthdate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String provider;

    private String providerId;

    @Builder
    public User(String name, String email, String nickname, String gender,
                String university, String department, String birthdate,
                Role role, String provider, String providerId) {
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;
        this.university = university;
        this.department = department;
        this.birthdate = birthdate;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    public User update(String name) {
        this.name = name;
        return this;
    }

    public User updateAdditionalInfo(String name, String nickname, String email,
                                     String gender, String university,
                                     String department, String birthdate) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.university = university;
        this.department = department;
        this.birthdate = birthdate;
        this.role = Role.USER;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
