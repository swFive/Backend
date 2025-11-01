package com.example.medicineReminder.domain.repository;

import com.example.medicineReminder.domain.entity.IntakeSchedules;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface IntakeSchedulesRepository extends JpaRepository<IntakeSchedules, Long>
{
    List<IntakeSchedules> findByUserIdAndIsActiveTrueOrderByIntakeTimeAsc(Long userId);

    List<IntakeSchedules> findByUserIdAndIntakeTimeBetween(Long userId, LocalTime start, LocalTime end);
}
