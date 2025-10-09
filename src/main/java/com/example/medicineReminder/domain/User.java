package com.example.medicineReminder.domain;

import jakarta.persistence.*; // jakarta.persistence.* 로 임포트
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "appusers")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") // 2. 'id' 필드를 DB의 'user_id' 컬럼과 연결합니다.
    private Long id;

    // 3. AppUsers 테이블에 'kakao_id' 컬럼을 추가해야 합니다. (예: kakao_id BIGINT)
    // 이 필드는 카카오 로그인을 구현하는 데 필수적입니다.
    private Long kakaoId;

    @Column(name = "name") // 4. 'nickname' 필드를 DB의 'name' 컬럼과 연결합니다.
    private String nickname;

    @Builder
    public User(Long kakaoId, String nickname) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
    }
}
