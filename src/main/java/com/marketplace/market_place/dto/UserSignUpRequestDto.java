package com.marketplace.market_place.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignUpRequestDto {
    private String email;
    private String name;
    private String nickname;
    private String gender;
    private String university;
    private String department;
    private String birthdate;

    @Builder
    public UserSignUpRequestDto(String name, String nickname, String gender, String university, String department, String birthdate) {
        this.name = name;
        this.nickname = nickname;
        this.gender = gender;
        this.university = university;
        this.department = department;
        this.birthdate = birthdate;
    }
}
