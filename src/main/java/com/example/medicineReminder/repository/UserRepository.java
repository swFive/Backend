package com.example.medicineReminder.repository;


import com.example.medicineReminder.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    // kakaoId를 통해 이미 가입된 사용자인지 확인하기 위한 메소드
    Optional<AppUser> findByKakaoId(Long kakaoId);
}