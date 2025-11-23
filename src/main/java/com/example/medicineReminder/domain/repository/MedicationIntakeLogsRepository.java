package com.example.medicineReminder.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;



import com.example.medicineReminder.domain.entity.MedicationIntakeLogs;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface MedicationIntakeLogsRepository extends JpaRepository<MedicationIntakeLogs, Long> {

    @Query(value = """
        SELECT COUNT(*) > 0
        FROM MedicationIntakeLogs
        WHERE user_id = :userId
          AND schedule_id = :scheduleId
          AND intake_status IN ('TAKEN','LATE')
          AND record_time >= :since
        """, nativeQuery = true)
    boolean existsTakenOrLateSince(@Param("userId") Long userId,
                                   @Param("scheduleId") Long scheduleId,
                                   @Param("since") LocalDateTime since);
}
