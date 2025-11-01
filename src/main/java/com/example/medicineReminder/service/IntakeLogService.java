// src/main/java/com/example/medicineReminder/service/IntakeLogService.java
package com.example.medicineReminder.service;

import com.example.medicineReminder.domain.entity.MedicationIntakeLogs;
import com.example.medicineReminder.domain.model.IntakeStatus;
import com.example.medicineReminder.domain.repository.MedicationIntakeLogsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class IntakeLogService
{
    private final MedicationIntakeLogsRepository repo;

    public IntakeLogService(MedicationIntakeLogsRepository repo)
    {
        this.repo = repo;
    }

    @Transactional
    public MedicationIntakeLogs create(Long scheduleId, Long userId, IntakeStatus status, Integer lateMinutes)
    {
        if (status != IntakeStatus.LATE)
        {
            lateMinutes = (lateMinutes == null) ? 0 : lateMinutes;
        }
        else
        {
            if (lateMinutes == null || lateMinutes < 0)
            {
                throw new IllegalArgumentException("lateMinutes must be >= 0 when status is LATE");
            }
        }

        MedicationIntakeLogs log = new MedicationIntakeLogs();
        log.setScheduleId(scheduleId);
        log.setUserId(userId);
        log.setRecordTime(LocalDateTime.now());
        log.setIntakeStatus(status);
        log.setLateMinutes(lateMinutes);

        return repo.save(log);
    }
}
