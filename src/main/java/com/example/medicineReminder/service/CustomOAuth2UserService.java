package com.example.medicineReminder.service;

// CustomOAuth2UserService.java

import com.example.medicineReminder.domain.PrincipalDetails;
import com.example.medicineReminder.domain.AppUser;
import com.example.medicineReminder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // ... (회원 확인 및 가입 로직은 그대로) ...
        Long kakaoId = (Long) oAuth2User.getAttributes().get("id");
        String nickname = (String) ((Map)oAuth2User.getAttributes().get("properties")).get("nickname");

        log.info("카카오 사용자 정보 확인: kakaoId={}, nickname={}", kakaoId, nickname);
        AppUser user = userRepository.findByKakaoId(kakaoId)
                .map(existingUser -> {
                    // 기존 사용자가 존재할 경우
                    log.info("기존 회원입니다. DB에서 사용자를 조회합니다: {}", existingUser.getNickname());
                    return existingUser;
                })
                .orElseGet(() -> {
                    log.info("신규 회원입니다. DB에 사용자를 저장합니다.");
                    AppUser newUser = AppUser.builder()
                            .kakaoId(kakaoId)
                            .nickname(nickname)
                            .build();
                    return userRepository.save(newUser);
                });

        // ▼▼▼ 이 부분이 변경됩니다 ▼▼▼
        // 기존: return oAuth2User;
        // 변경: User 객체와 attributes를 담은 PrincipalDetails 객체를 생성하여 반환
        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}
