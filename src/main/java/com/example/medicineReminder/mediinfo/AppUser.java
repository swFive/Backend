// (패키지 경로는 기존 파일들과 맞춰주세요)
package com.example.medicineReminder.mediinfo; // 또는 user 패키지

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// 엔티티 이름을 "MediInfoAppUser"로 명시해서 domain.AppUser와 충돌 방지
@Entity(name = "MediInfoAppUser")
@Getter
@Setter
@Table(name = "AppUsers")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "kakao_id", nullable = false, unique = true)
    private Long kakaoId;

    @Column(nullable = false, length = 50)
    private String name;
}
