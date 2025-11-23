package com.example.medicineReminder.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "AppUsers")
public class AppUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "kakao_id", nullable = false, unique = true)
    private Long kakaoId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    public AppUsers(Long kakaoId, String name) {
        this.kakaoId = kakaoId;
        this.name = name;
    }
}
