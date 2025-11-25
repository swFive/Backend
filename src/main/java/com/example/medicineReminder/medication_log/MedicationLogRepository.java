package com.example.medicineReminder.medication_log;

import com.example.medicineReminder.web.dto.Statistics.MedicationStatisticsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // [수정]
import org.springframework.data.repository.query.Param; // [수정]

import java.time.LocalDate;
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

    @Query(value = """
    SELECT
        U.name AS userName,
        DATE(L.record_time) AS date,
        CAST(COALESCE(COUNT(L.log_id), 0) AS SIGNED) AS totalRecords,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status IN ('TAKEN', 'LATE') THEN 1 ELSE 0 END), 0) AS SIGNED) AS successCount,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status = 'TAKEN' THEN 1 ELSE 0 END), 0) AS SIGNED) AS onTimeCount,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status = 'LATE' THEN 1 ELSE 0 END), 0) AS SIGNED) AS lateCount,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status IN ('LATE', 'SKIPPED') THEN 1 ELSE 0 END), 0) AS SIGNED) AS failureCount,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status = 'SKIPPED' THEN 1 ELSE 0 END), 0) AS SIGNED) AS skippedCount,
        
        ROUND(SUM(CASE WHEN L.intake_status IN ('TAKEN', 'LATE') THEN 1.0 ELSE 0.0 END) * 100.0 / NULLIF(COUNT(L.log_id),0),1) AS successRate,
        ROUND(SUM(CASE WHEN L.intake_status IN ('LATE', 'SKIPPED') THEN 1.0 ELSE 0.0 END) * 100.0 / NULLIF(COUNT(L.log_id),0),1) AS failureRate
    FROM
        MedicationIntakeLogs L
    JOIN
        AppUsers U ON L.user_id = U.user_id
    WHERE
        U.user_id = :userId
        AND L.record_time >= :startDateTime
        AND L.record_time < :endDateTime
    GROUP BY
        DATE(L.record_time), U.user_id, U.name
    ORDER BY
        date ASC
    """, nativeQuery = true)
    List<MedicationStatisticsDto> findMedicationStatisticsByDateRange(
            @Param("userId") Long userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    // 2. 🚀 [추가된 메서드] 주/월별 총합 통계용 쿼리 (GROUP BY DATE 제거)
    @Query(value = """
    SELECT
        U.name AS userName,
        DATE(:startDateTime) AS date, 
        
        -- 💡 [수정 적용] Long 타입 매핑 및 NULL 방지를 위해 CAST와 COALESCE 적용
        CAST(COALESCE(COUNT(L.log_id), 0) AS SIGNED) AS totalRecords,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status IN ('TAKEN', 'LATE') THEN 1 ELSE 0 END), 0) AS SIGNED) AS successCount,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status = 'TAKEN' THEN 1 ELSE 0 END), 0) AS SIGNED) AS onTimeCount,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status = 'LATE' THEN 1 ELSE 0 END), 0) AS SIGNED) AS lateCount,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status IN ('LATE', 'SKIPPED') THEN 1 ELSE 0 END), 0) AS SIGNED) AS failureCount,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status = 'SKIPPED' THEN 1 ELSE 0 END), 0) AS SIGNED) AS skippedCount,
        
        ROUND(SUM(CASE WHEN L.intake_status IN ('TAKEN', 'LATE') THEN 1.0 ELSE 0.0 END) * 100.0 / NULLIF(COUNT(L.log_id),0),1) AS successRate,
        ROUND(SUM(CASE WHEN L.intake_status IN ('LATE', 'SKIPPED') THEN 1.0 ELSE 0.0 END) * 100.0 / NULLIF(COUNT(L.log_id),0),1) AS failureRate
    FROM
        MedicationIntakeLogs L
    JOIN
        AppUsers U ON L.user_id = U.user_id
    WHERE
        U.user_id = :userId
        AND L.record_time >= :startDateTime
        AND L.record_time < :endDateTime
        
    GROUP BY
        U.user_id, U.name
    ORDER BY
        U.name ASC
    """, nativeQuery = true)
    List<MedicationStatisticsDto> findAggregateStatisticsByDateRange(
            @Param("userId") Long userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    //TEXT
    @Query(value = """
    SELECT
        U.name AS userName,
        DATE(:startDateTime) AS date, 
        CAST(COALESCE(COUNT(L.log_id), 0) AS SIGNED) AS totalRecords,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status IN ('TAKEN', 'LATE') THEN 1 ELSE 0 END), 0) AS SIGNED) AS successCount,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status = 'TAKEN' THEN 1 ELSE 0 END), 0) AS SIGNED) AS onTimeCount,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status = 'LATE' THEN 1 ELSE 0 END), 0) AS SIGNED) AS lateCount,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status IN ('LATE', 'SKIPPED') THEN 1 ELSE 0 END), 0) AS SIGNED) AS failureCount,
        CAST(COALESCE(SUM(CASE WHEN L.intake_status = 'SKIPPED' THEN 1 ELSE 0 END), 0) AS SIGNED) AS skippedCount,
        ROUND(SUM(CASE WHEN L.intake_status IN ('TAKEN', 'LATE') THEN 1.0 ELSE 0.0 END) * 100.0 / NULLIF(COUNT(L.log_id),0),1) AS successRate,
        ROUND(SUM(CASE WHEN L.intake_status IN ('LATE', 'SKIPPED') THEN 1.0 ELSE 0.0 END) * 100.0 / NULLIF(COUNT(L.log_id),0),1) AS failureRate
    FROM
        MedicationIntakeLogs L
    JOIN
        AppUsers U ON L.user_id = U.user_id
    WHERE
        U.user_id = :userId
        AND L.record_time >= :startDateTime
        AND L.record_time < :endDateTime
    GROUP BY
        U.user_id, U.name
    ORDER BY
        U.name ASC
    """, nativeQuery = true)
    List<Object[]> findAggregateStatisticsRaw(
            @Param("userId") Long userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    // 3. 🚀 [추가된 메서드] 약물별 미복용 TOP 3 통계 쿼리 (주석 제거 완료)
    // 결과를 Object[]로 받아 서비스에서 MedicationFailureDto로 직접 매핑합니다.
    @Query(value = """
    SELECT
        M.name AS medicationName, 
        -- 실패 횟수 (LATE + SKIPPED) / 전체 시도 횟수 * 100.0
        ROUND(SUM(CASE WHEN L.intake_status IN ('LATE', 'SKIPPED') THEN 1.0 ELSE 0.0 END) * 100.0 / NULLIF(COUNT(L.log_id), 0), 1) AS failureRate
    FROM
        MedicationIntakeLogs L
    -- L.schedule_id를 통해 IntakeSchedules (S)에 접근
    JOIN
        IntakeSchedules S ON L.schedule_id = S.schedule_id
    -- S.medication_id를 통해 UserMedications (M)에 접근하여 약물명 조회
    JOIN
        UserMedications M ON S.medication_id = M.medication_id
    WHERE
        L.user_id = :userId
    GROUP BY
        M.medication_id, M.name 
    ORDER BY
        failureRate DESC
    LIMIT 3
    """, nativeQuery = true)
    List<Object[]> findTopMissedMedicationsRaw(@Param("userId") Long userId);
}