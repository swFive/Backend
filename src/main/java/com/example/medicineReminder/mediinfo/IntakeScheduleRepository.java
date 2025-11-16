package com.example.medicineReminder.mediinfo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntakeScheduleRepository extends JpaRepository<IntakeSchedule, Long> {
    List<IntakeSchedule> findByMedicationId(Long medicationId);

}