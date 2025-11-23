package com.example.medicineReminder.filter;

import com.example.medicineReminder.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    // Authorization 헤더에서 JWT를 추출할 때 사용할 PREFIX
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * HTTP 요청 헤더에서 JWT 토큰을 추출합니다.
     * @param request HTTP 요청 객체
     * @return 토큰 문자열 (null 가능)
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            // "Bearer " 접두사를 제거하고 토큰 본문만 반환
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 모든 요청마다 실행되어 토큰을 검증하고 인증 정보를 설정합니다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더에서 JWT 토큰 추출
        String jwt = resolveToken(request);

        // 2. 토큰 유효성 검사
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

            // 3. 토큰이 유효하면 인증 정보(Authentication) 획득
            Authentication authentication = tokenProvider.getAuthentication(jwt);

            // 4. Spring Security Context에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}