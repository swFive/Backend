package com.example.medicineReminder.config;

import com.example.medicineReminder.service.CustomOAuth2UserService;
// 🔑 1. JwtAuthenticationFilter의 정확한 경로를 사용합니다. (패키지 구조에 따라 수정 필요)
import com.example.medicineReminder.filter.JwtAuthenticationFilter;
// 🔑 2. OAuth2AuthenticationSuccessHandler의 정확한 경로를 사용합니다. (패키지 구조에 따라 수정 필요)
import com.example.medicineReminder.handler.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // 필터 위치 지정용
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    // 🔑 3. 생성자 주입을 통해 Bean을 가져옵니다. (해당 클래스에 @Component가 있어야 함)
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1. CORS 설정 활성화
                .cors(Customizer.withDefaults())

                // 2. API 서버를 위해 CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // 🔑 3. JWT를 사용하므로 세션을 STATELESS로 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 🔑 4. JWT 필터를 UsernamePasswordAuthenticationFilter 전에 추가하여 매 요청마다 JWT를 검증
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        // 5. 인증 제외 경로 (permitAll)
                        .requestMatchers(
                                "/", "/login", "/signup", "/api/v1/users/register",
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html"
                        ).permitAll()

                        // 6. 인증 필요 경로 (authenticated)
                        .requestMatchers("/my-info").authenticated()
                        .requestMatchers("/api/v1/**").authenticated()
                        .anyRequest().authenticated()
                )

                // 7. OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        // 🔑 로그인 성공 시 JWT를 생성하는 핸들러 등록
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .userInfoEndpoint(config -> config.userService(customOAuth2UserService))
                );

        // 8. 로그아웃 설정 (JWT는 세션이 없으므로 주로 클라이언트에서 토큰을 제거하는 방식 사용)
        http.logout(AbstractHttpConfigurer::disable);


        return http.build();
    }

    /**
     * 프론트엔드 개발 환경(localhost:63342 등)을 허용하는 CORS 설정 Bean
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:63342",
                "http://localhost",
                "http://127.0.0.1"
        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}