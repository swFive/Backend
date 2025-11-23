package com.example.medicineReminder.medication_log;

// === [핵심 수정] 'mediinfo'가 아닌 'IntakeJournal' 패키지에서 가져오기 ===
import com.example.medicineReminder.IntakeJournal.IntakeJournal;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "MedicationIntakeLogs")
public class MedicationIntakeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id")
    private IntakeJournal journal;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "record_time")
    private LocalDateTime recordTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "intake_status", nullable = false)
    private IntakeStatus intakeStatus;

    @Column(name = "late_minutes")
    private Integer lateMinutes;

    // (이모지, 메모는 IntakeJournal이 관리하므로 여기서 제거)

    public enum IntakeStatus {
        TAKEN,
        SKIPPED,
        LATE
    }
}