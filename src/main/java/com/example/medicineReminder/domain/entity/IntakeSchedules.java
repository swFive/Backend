package com.example.medicineReminder.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "IntakeSchedules",
        indexes = {
                @Index(name = "idx_schedule_user_time", columnList = "user_id,intake_time"),
                @Index(name = "idx_schedule_active", columnList = "user_id,is_active")
        })
public class IntakeSchedules
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "medication_id", nullable = false)
    private Long medicationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "intake_time", nullable = false)
    private LocalTime intakeTime;

    @Column(name = "frequency", nullable = false, length = 50)
    private String frequency;

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

    public Long getScheduleId()
    {
        return scheduleId;
    }

    // 최소 세터들
    public void setMedicationId(Long medicationId)
    {
        this.medicationId = medicationId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public void setIntakeTime(LocalTime intakeTime)
    {
        this.intakeTime = intakeTime;
    }

    public void setFrequency(String frequency)
    {
        this.frequency = frequency;
    }

    public void setStartDate(LocalDate startDate)
    {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate)
    {
        this.endDate = endDate;
    }

    public void setLeadMinutes(Integer leadMinutes)
    {
        this.leadMinutes = leadMinutes;
    }

    public void setIsRepeat(Boolean isRepeat)
    {
        this.isRepeat = isRepeat;
    }

    public void setReNotifyEnabled(Boolean reNotifyEnabled)
    {
        this.reNotifyEnabled = reNotifyEnabled;
    }

    public void setReNotifyIntervalMin(Integer reNotifyIntervalMin)
    {
        this.reNotifyIntervalMin = reNotifyIntervalMin;
    }

    public void setIsActive(Boolean isActive)
    {
        this.isActive = isActive;
    }

    // 조회용 게터 몇 개
    public Long getUserId()
    {
        return userId;
    }

    public LocalTime getIntakeTime()
    {
        return intakeTime;
    }

    public Boolean getIsActive()
    {
        return isActive;
    }

    public Boolean getReNotifyEnabled()
    {
        return reNotifyEnabled;
    }

    public Integer getReNotifyIntervalMin()
    {
        return reNotifyIntervalMin;
    }
}
