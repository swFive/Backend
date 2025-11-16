package com.example.medicineReminder.mediinfo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore; // === [1. 이거 추가] ===
import com.example.medicineReminder.mediinfo.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "IntakeSchedules")
public class IntakeSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    private UserMedication medication;

    // === [2. 여기에 @JsonIgnore 추가] ===
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "intake_time", nullable = false)
    private LocalTime intakeTime;

    @Column(nullable = false, length = 50)
    private String frequency; // (예: "DAILY", "월,수,금")

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "lead_minutes", nullable = false)
    private Integer leadMinutes = 0;

    @Column(name = "is_repeat", nullable = false)
    private Boolean isRepeat = true;

    @Column(name = "re_notify_enabled", nullable = false)
    private Boolean reNotifyEnabled = true;

    @Column(name = "re_notify_interval_min", nullable = false)
    private Integer reNotifyIntervalMin = 5;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}