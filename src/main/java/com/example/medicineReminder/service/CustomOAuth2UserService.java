package com.example.medicineReminder.service;

// CustomOAuth2UserService.java

import com.example.medicineReminder.domain.User;
import com.example.medicineReminder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository; // UserRepository를 주입받음

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 1. 카카오로부터 사용자 정보 가져오기
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Long kakaoId = (Long) attributes.get("id");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickname = (String) properties.get("nickname");

        // 2. 제공받은 정보로 서비스 회원 여부 확인 (DB 조회)
        Optional<User> optionalUser = userRepository.findByKakaoId(kakaoId);

        User user;
        if (optionalUser.isPresent()) {
            // 이미 가입된 경우: 기존 사용자 정보를 가져옴
            user = optionalUser.get();
            System.out.println("✅ 기존 회원입니다: " + user.getNickname());
        } else {
            // 신규 사용자인 경우: 회원 가입 처리
            user = User.builder()
                    .kakaoId(kakaoId)
                    .nickname(nickname)
                    .build();
            userRepository.save(user);
            System.out.println("✅ 신규 회원 자동 가입: " + user.getNickname());
        }

        // Spring Security가 인증을 처리할 수 있도록 OAuth2User 객체를 반환
        // (세부 구현에 따라 User 정보를 담은 커스텀 객체를 반환하는 것이 일반적)
        return oAuth2User;
    }
}