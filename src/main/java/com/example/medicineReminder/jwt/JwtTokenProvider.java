package com.example.medicineReminder.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtTokenProvider {

    private final Key key;
    private final long tokenValidityInMilliseconds;

    // application.yml 또는 application.properties에서 JWT 설정 값 주입
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.token-validity-in-seconds}") long tokenValiditySeconds) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds = tokenValiditySeconds * 1000;
    }

    /**
     * 사용자 ID를 기반으로 JWT 토큰을 생성합니다.
     * @param userId AppUser의 내부 ID (Long)
     * @return 생성된 JWT 문자열
     */
    public String createToken(Long userId) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 토큰 주체: 사용자 ID
                .claim("auth", "ROLE_USER") // 권한 정보 (예시)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity) // 만료 시간 설정
                .compact();
    }

    /**
     * JWT 토큰을 복호화하여 인증 객체(Authentication)를 생성합니다.
     * @param token JWT 토큰 문자열
     * @return Spring Security Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 토큰에서 추출한 ID를 기반으로 PrincipalDetails를 대체할 임시 Principal 생성
        // 실제로는 DB에서 사용자 정보를 다시 로드하는 것이 보안상 더 좋습니다.
        String userId = claims.getSubject();

        // 권한 설정 (단순 ROLE_USER 부여)
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        // Principal은 임시로 userId를 사용하고, 추후 CustomUserDetailsService에서 로드 가능
        return new UsernamePasswordAuthenticationToken(userId, token, authorities);
    }

    /**
     * 토큰의 유효성을 검증합니다.
     * @param token JWT 토큰 문자열
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 토큰 파싱/검증 실패 시
            return false;
        }
    }
}
