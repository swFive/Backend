package com.example.medicineReminder.mediinfo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "UserMedications") // DB 스키마와 테이블명 일치
public class UserMedication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medication_id")
    private Long id;

    @JsonIgnore // (Lazy-loading 프록시 객체 JSON 변환 오류 방지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String category;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    // === DB 스키마에 존재하는 컬럼들 ===
    @Column(name = "initial_quantity")
    private Integer initialQuantity;

    @Column(name = "current_quantity")
    private Integer currentQuantity;

    @Column(name = "dose_unit_quantity")
    private Integer doseUnitQuantity;

    @Column(name = "refill_threshold")
    private Integer refillThreshold;

    @Column(name = "last_recalculated_at")
    private LocalDateTime lastRecalculatedAt;

    // === [핵심 수정] ===
    // 'frequency'는 DB에 없으므로 제거(주석 처리)합니다.
    /*
    @Column(length = 255)
    private String frequency;
    */

    // 'memo'는 DB에 있으므로 남겨둡니다.
    @Column(columnDefinition = "TEXT")
    private String memo;

    @JsonManagedReference // (JSON 무한 루프 방지)
    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IntakeSchedule> schedules;
}