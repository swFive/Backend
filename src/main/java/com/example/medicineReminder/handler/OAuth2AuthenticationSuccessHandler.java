package com.example.medicineReminder.handler;

import com.example.medicineReminder.jwt.JwtTokenProvider;
import com.example.medicineReminder.domain.PrincipalDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component // Spring Bean으로 등록
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    // application.yml에서 프론트엔드 리다이렉트 URI를 주입받도록 변경
    @Value("${app.oauth2.redirect-uri}")
    private String REDIRECT_URI;

    /**
     * OAuth2 로그인 성공 시 호출되며, JWT를 생성하여 클라이언트에게 리다이렉트합니다.
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // 1. 인증된 사용자 객체 (PrincipalDetails) 획득
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long userId = principal.getUser().getId();

        // 2. 획득한 사용자 ID 기반으로 JWT 생성
        String jwtToken = tokenProvider.createToken(userId);

        // 3. JWT를 쿼리 파라미터로 포함하여 프론트엔드 URL 생성
        String targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
                .queryParam("token", jwtToken) // 토큰을 쿼리 파라미터로 클라이언트에게 전달
                .build().toUriString();

        // 4. 클라이언트를 생성된 URL로 리다이렉트
        response.sendRedirect(targetUrl);
    }
}
