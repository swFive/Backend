// (패키지 경로는 기존 파일들과 맞춰주세요)
package com.example.medicineReminder.mediinfo; // 또는 user 패키지

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
}