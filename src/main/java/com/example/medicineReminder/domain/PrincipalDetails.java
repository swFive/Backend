// PrincipalDetails.java

package com.example.medicineReminder.domain;

import com.example.medicineReminder.domain.entity.AppUsers;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class PrincipalDetails implements OAuth2User {

    private final AppUsers user;
    private final Map<String, Object> attributes;

    // OAuth2 로그인 시 사용되는 생성자
    public PrincipalDetails(AppUsers user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // 우리 서비스의 User 객체를 반환하는 메소드
    public AppUsers getUser() {
        return user;
    }

    // === OAuth2User 인터페이스의 메소드 구현 ===

    @Override
    public Map<String, Object> getAttributes() {
        return attributes; // 카카오로부터 받은 사용자 정보
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자의 권한을 반환 (여기서는 기본적으로 "ROLE_USER"를 부여)
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        // application.yml에서 설정한 user-name-attribute 값(id)에 해당
        return String.valueOf(user.getKakaoId());
    }
}