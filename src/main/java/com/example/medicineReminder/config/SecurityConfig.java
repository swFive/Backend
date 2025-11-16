package com.example.medicineReminder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import com.example.medicineReminder.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // CorsConfig가 있으면 그대로 사용
                .authorizeHttpRequests(auth -> auth
                        // swagger / api-docs / 에러 / 헬스체크 등 완전 개방
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/actuator/**",
                                "/error",
                                "/api/**"
                        ).permitAll()
                        .requestMatchers("/", "/login").permitAll() // 루트 경로와 로그인 경로는 모두 허용
                        .anyRequest().permitAll() // 개발용: 전부 개방
                )
                .httpBasic(b -> b.disable())
                .formLogin(f -> f.disable())
                .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                        // 로그인 성공 후 처리는 우리가 만든 customOAuth2UserService를 사용하라고 지정
                        .userService(customOAuth2UserService)
                )
        );

        return http.build();
    }
}
