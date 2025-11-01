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
@Table(name = "UserNotificationSettings")
public class UserNotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long settingId;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "notify_time_offset", nullable = false)
    private Integer notifyTimeOffset = 0;

    @Column(name = "is_repeat", nullable = false)
    private Boolean isRepeat = true;

    @Column(name = "re_notify_interval", nullable = false)
    private Integer reNotifyInterval = 5;

    // DB 기본값/ON UPDATE에 맡김
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
