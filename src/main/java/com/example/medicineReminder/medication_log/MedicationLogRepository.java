package com.example.medicineReminder.medication_log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // [수정]
import org.springframework.data.repository.query.Param; // [수정]
import java.time.LocalDateTime; // [수정]
import java.util.List;

public interface MedicationLogRepository extends JpaRepository<MedicationIntakeLog, Long> {

    // === [핵심 수정] "캘린더"와 "약 목록(다음 복용시간)" 기능에 필수적인 쿼리 복구 ===
    @Query("SELECT m FROM MedicationIntakeLog m WHERE m.scheduleId IN :scheduleIds AND m.recordTime >= :startOfDay AND m.recordTime < :endOfDay")
    List<MedicationIntakeLog> findLogsByScheduleIdsAndDate(
            @Param("scheduleIds") List<Long> scheduleIds,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    // (기존) 특정 약의 전체 로그 조회 쿼리 (medication_list.html의 "기록 보기"에서 사용)
    List<MedicationIntakeLog> findByScheduleIdInOrderByRecordTimeDesc(List<Long> scheduleIds);
}