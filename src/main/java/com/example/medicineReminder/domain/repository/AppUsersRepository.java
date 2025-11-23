package com.example.medicineReminder.domain.repository;

import com.example.medicineReminder.domain.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppUsersRepository extends JpaRepository<AppUsers, Long> {
    Optional<AppUsers> findByKakaoId(Long kakaoId);
}
