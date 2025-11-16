package com.example.medicineReminder.mediinfo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserMedicationRepository extends JpaRepository<UserMedication, Long> {

    // (findAll()은 모든 사용자의 약을 가져오므로 주석 처리하거나 삭제)
    // @Override
    // @EntityGraph(attributePaths = {"schedules"})
    // List<UserMedication> findAll();

    // === 핵심: userId로 본인의 약만 조회하도록 변경 ===
    @EntityGraph(attributePaths = {"schedules"})
    List<UserMedication> findByUserId(Long userId);
}