package com.marketplace.market_place.config;

import com.marketplace.market_place.handler.OAuth2AuthenticationSuccessHandler;
import com.marketplace.market_place.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.core.annotation.Order; // ✅ 이거여야 함

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean(name = "defaultFilterChain")
    @Order(2) // prod 체인 다음
    public SecurityFilterChain defaultChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**") // 나머지 전체 담당 (api는 1번 체인이 먼저 잡음)
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**",
                                "/h2-console/**", "/signup", "/api/auth/signup").permitAll()
                        .requestMatchers("/api/v1/stores/search").permitAll() // 필요시 유지
                        .requestMatchers("/api/v1/**").hasRole("USER")         // 기존 정책 유지
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout.logoutSuccessUrl("https://www.google.com"))
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                );
        return http.build();
    }
}