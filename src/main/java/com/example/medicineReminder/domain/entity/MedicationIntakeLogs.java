// src/main/java/com/example/medicineReminder/domain/entity/MedicationIntakeLogs.java
package com.example.medicineReminder.domain.entity;

import com.example.medicineReminder.domain.model.IntakeStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MedicationIntakeLogs",
        indexes = {
                @Index(name = "idx_intake_user_time", columnList = "user_id,record_time"),
                @Index(name = "idx_intake_schedule", columnList = "schedule_id")
        })
public class MedicationIntakeLogs
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "record_time")
    private LocalDateTime recordTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "intake_status", nullable = false, length = 20)
    private IntakeStatus intakeStatus;

    @Column(name = "late_minutes")
    private Integer lateMinutes;

    public Long getLogId()
    {
        return logId;
    }

    public Long getScheduleId()
    {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId)
    {
        this.scheduleId = scheduleId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public LocalDateTime getRecordTime()
    {
        return recordTime;
    }

    public void setRecordTime(LocalDateTime recordTime)
    {
        this.recordTime = recordTime;
    }

    public IntakeStatus getIntakeStatus()
    {
        return intakeStatus;
    }

    public void setIntakeStatus(IntakeStatus intakeStatus)
    {
        this.intakeStatus = intakeStatus;
    }

    public Integer getLateMinutes()
    {
        return lateMinutes;
    }

    public void setLateMinutes(Integer lateMinutes)
    {
        this.lateMinutes = lateMinutes;
    }
}
