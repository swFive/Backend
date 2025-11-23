package com.example.medicineReminder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import com.example.medicineReminder.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 🚨 CORS 설정 활성화
                .cors(Customizer.withDefaults())
                // CSRF 비활성화 (API 서버의 일반적인 설정)
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authorize -> authorize
                        // Swagger UI 경로 접근 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 루트 경로와 로그인 경로는 모두 허용
                        .requestMatchers("/", "/login").permitAll()
                        .anyRequest().authenticated() // 그 외의 모든 요청은 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                // 로그인 성공 후 처리는 우리가 만든 customOAuth2UserService를 사용하라고 지정
                                .userService(customOAuth2UserService)
                        )
                );

        return http.build();
    }

    // 2. 🚨 CORS 구체적 설정 Bean 정의
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🔑 프론트엔드 주소(localhost:63342)를 허용 목록에 추가
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:63342"));

        // 필요한 HTTP 메서드 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 인증 정보(세션, Authorization 헤더 등) 전송 허용
        configuration.setAllowCredentials(true);

        // 모든 헤더를 허용 (Authorization 헤더를 포함하기 위함)
        configuration.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 API 경로에 CORS 규칙 적용
        return source;
    }
}