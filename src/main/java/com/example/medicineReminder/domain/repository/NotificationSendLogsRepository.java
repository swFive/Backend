package com.example.medicineReminder.domain.repository;

import com.example.medicineReminder.domain.entity.NotificationSendLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface NotificationSendLogsRepository extends JpaRepository<NotificationSendLogs, Long> {

    @Query(value = """
        SELECT MAX(created_at) FROM NotificationSendLogs
        WHERE user_id = :userId
          AND JSON_EXTRACT(data_json, '$.scheduleId') = CAST(:scheduleId AS JSON)
    """, nativeQuery = true)
    LocalDateTime findLastSentTimeByUserAndSchedule(@Param("userId") Long userId,
                                                    @Param("scheduleId") Long scheduleId);
}