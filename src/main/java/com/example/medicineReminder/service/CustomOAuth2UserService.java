package com.example.medicineReminder.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2AccessToken accessToken = userRequest.getAccessToken();

        System.out.println("==============================================");
        System.out.println("✅ 카카오 액세스 토큰 값: " + accessToken.getTokenValue());
        System.out.println("✅ 토큰 만료 시간: " + accessToken.getExpiresAt());
        System.out.println("==============================================");
        /*

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 카카오로부터 받은 사용자 정보
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");

        System.out.println("카카오 사용자 정보: " + attributes);
        System.out.println("이메일: " + email);
        System.out.println("닉네임: " + nickname);

        // 여기서 DB에 사용자 정보 저장 또는 업데이트 로직을 구현합니다.
        // 예를 들어, 이메일을 기반으로 사용자가 이미 존재하는지 확인하고,
        // 존재하지 않으면 새로 회원가입 처리, 존재하면 정보를 업데이트 할 수 있습니다.*/

        return super.loadUser(userRequest);
    }
}