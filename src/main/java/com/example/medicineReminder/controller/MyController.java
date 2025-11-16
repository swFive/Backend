package com.example.medicineReminder.controller;

import com.example.medicineReminder.domain.PrincipalDetails;
import com.example.medicineReminder.domain.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @GetMapping("/my-info")
    public String getMyInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        // @AuthenticationPrincipal 어노테이션으로 세션에 저장된 PrincipalDetails 객체를 바로 주입받음

        // 우리 서비스의 User 객체를 꺼내서 사용
        User user = principalDetails.getUser();

        Long myInternalId = user.getId(); // 우리 DB의 user_id
        String myNickname = user.getNickname();

        return "내부 ID: " + myInternalId + ", 닉네임: " + myNickname;
    }
}