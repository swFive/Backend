// src/main/java/com/example/medicineReminder/web/dto/intake/IntakeLogResponse.java
package com.example.medicineReminder.web.dto.intake;

import com.example.medicineReminder.domain.entity.MedicationIntakeLogs;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;


public record IntakeLogResponse(
        Long logId,
        Long scheduleId,
        Long userId,
        String intakeStatus,
        Integer lateMinutes,
        LocalDateTime recordTime
)
{
    public static IntakeLogResponse from(MedicationIntakeLogs l)
    {
        return new IntakeLogResponse(
                l.getLogId(),
                l.getScheduleId(),
                l.getUserId(),
                l.getIntakeStatus() == null ? null : l.getIntakeStatus().name(),
                l.getLateMinutes(),
                l.getRecordTime()
        );
    }
}
