package com.marketplace.market_place.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.core.annotation.Order; // ✅ 이거여야 함

@Configuration
@Profile("prod") // prod에서만 이 체인도 로드 (원하면 빼도 됨)
public class SecurityConfigProd {

    @Bean(name = "prodFilterChain")
    @Order(1) // 먼저 매칭
    public SecurityFilterChain prodChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // ✅ /api/** 만 담당
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/posts/**").permitAll()
                        .requestMatchers("/api/v1/**").permitAll()
                        .requestMatchers("/mypage/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/").permitAll())
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/")
                        .invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll());
        return http.build();
    }
}