package com.marketplace.market_place.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("prod")
public class SecurityConfigProd {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/posts/**").authenticated() // 게시글 관련 인증 필요
                        .requestMatchers("/mypage/**").authenticated() // 마이페이지 인증 필요
                        .requestMatchers("/api/v1/**").permitAll() // 기본 CRUD는 허용
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1) // 중복 로그인 방지
                        .maxSessionsPreventsLogin(false)
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }
}