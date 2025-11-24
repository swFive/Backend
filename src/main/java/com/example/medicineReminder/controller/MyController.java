package com.example.medicineReminder.controller;

import com.example.medicineReminder.domain.entity.AppUsers;
import com.example.medicineReminder.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor // final 필드(UserRepository) 주입을 위해 필수!
public class MyController {

    private final UserRepository userRepository; // DB 조회를 위해 추가

    @GetMapping("/my-info")
    // @AuthenticationPrincipal Object principal 로 변경 (String ID가 들어옴)
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal Object principal) {

        try {
            // 1. JWT 필터가 넘겨준 식별자(String 형태의 ID) 가져오기
            String userIdString = (String) principal;
            Long userId = Long.parseLong(userIdString);

            log.info("내 정보 요청: ID = {}", userId);

            // 2. ID로 DB에서 실제 유저 정보 조회
            AppUsers user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

            // 3. 프론트엔드가 좋아하는 JSON 형식(Map)으로 포장
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("kakaoId", user.getKakaoId());

            // {"id": 1, "nickname": "홍길동", ...} 형태로 반환됨
            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            log.error("내 정보 조회 중 오류 발생", e);
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // 로그아웃 로직은 기존과 동일 (프론트에서 토큰 삭제가 핵심이므로 여기선 OK만 줘도 됨)
        return ResponseEntity.ok("로그아웃 처리 완료");
    }

    // (보조 메서드) 토큰 추출
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}