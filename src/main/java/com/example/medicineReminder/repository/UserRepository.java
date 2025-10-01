package com.example.medicineReminder.repository;
// UserRepository.java


import com.example.medicineReminder.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // kakaoId를 통해 이미 가입된 사용자인지 확인하기 위한 메소드
    Optional<User> findByKakaoId(Long kakaoId);
}