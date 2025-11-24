package com.example.medicineReminder.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() { }

    public static Long getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        // JwtTokenProvider에서 setSubject(userId)로 저장했으므로, getName()은 userId(String)입니다.
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("인증된 사용자 ID 형식이 올바르지 않습니다.");
        }
    }
}