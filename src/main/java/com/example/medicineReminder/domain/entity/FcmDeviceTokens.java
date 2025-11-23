package com.example.medicineReminder.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "FcmDeviceTokens",
        indexes = { @Index(name = "idx_user_active", columnList = "user_id,is_active") },
        uniqueConstraints = @UniqueConstraint(name = "uk_fcm_token", columnNames = "fcm_token"))
public class FcmDeviceTokens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "fcm_token", nullable = false, length = 255)
    private String fcmToken;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_failure_at")
    private LocalDateTime lastFailureAt;

    // DB 기본값에 맡김
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // DB ON UPDATE에 맡김
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
