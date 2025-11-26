package com.example.medicineReminder.mediinfo;

import com.example.medicineReminder.domain.entity.AppUsers;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUsers user;

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


    // =========================================================
    // Getter Methods
    // =========================================================

    public Long getId() {
        return id;
    }

    public UserMedication getMedication() {
        return medication;
    }

    public AppUsers getUser() {
        return user;
    }

    public LocalTime getIntakeTime() {
        return intakeTime;
    }

    public String getFrequency() {
        return frequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Integer getLeadMinutes() {
        return leadMinutes;
    }

    public Boolean getIsRepeat() {
        return isRepeat;
    }

    public Boolean getReNotifyEnabled() {
        return reNotifyEnabled;
    }

    public Integer getReNotifyIntervalMin() {
        return reNotifyIntervalMin;
    }

    public Boolean getIsActive() {
        return isActive;
    }


    // =========================================================
    // Setter Methods
    // =========================================================

    public void setId(Long id) {
        this.id = id;
    }

    public void setMedication(UserMedication medication) {
        this.medication = medication;
    }

    public void setUser(AppUsers user) {
        this.user = user;
    }

    public void setIntakeTime(LocalTime intakeTime) {
        this.intakeTime = intakeTime;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setLeadMinutes(Integer leadMinutes) {
        this.leadMinutes = leadMinutes;
    }

    public void setIsRepeat(Boolean isRepeat) {
        this.isRepeat = isRepeat;
    }

    public void setReNotifyEnabled(Boolean reNotifyEnabled) {
        this.reNotifyEnabled = reNotifyEnabled;
    }

    public void setReNotifyIntervalMin(Integer reNotifyIntervalMin) {
        this.reNotifyIntervalMin = reNotifyIntervalMin;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}