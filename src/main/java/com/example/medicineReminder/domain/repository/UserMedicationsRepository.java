package com.example.medicineReminder.domain.repository;

import com.example.medicineReminder.domain.entity.UserMedications;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMedicationsRepository extends JpaRepository<UserMedications, Long> {
    List<UserMedications> findByUserIdOrderByMedicationIdAsc(Long userId);
}
