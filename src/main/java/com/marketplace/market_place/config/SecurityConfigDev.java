package com.marketplace.market_place.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("dev")
public class SecurityConfigDev {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()  // 여기가 핵심
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable()) // 세션 로그인도 비활성화
                .httpBasic(httpBasic -> httpBasic.disable());
        return http.build();
    }

}
