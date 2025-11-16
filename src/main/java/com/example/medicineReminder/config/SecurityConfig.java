package com.example.medicineReminder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 모든 요청에 대해 인증 없이 접근 허용
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                // CSRF 보호 끄기 (API나 테스트 시 편의용)
                .csrf(csrf -> csrf.disable())
                // 로그인 폼 및 기본 인증 비활성화
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
