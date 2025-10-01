package com.example.medicineReminder.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/login").permitAll() // 루트 경로와 로그인 경로는 모두 허용
                        .anyRequest().authenticated() // 그 외의 모든 요청은 인증 필요
                )
                .oauth2Login(withDefaults()); // OAuth 2.0 로그인 활성화

        return http.build();
    }
}
