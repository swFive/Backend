package com.example.medicineReminder.web.dto.intake;// web/dto/intake/IntakeLogCreateRequest.java
import com.example.medicineReminder.domain.model.IntakeStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record IntakeLogCreateRequest(
        Long scheduleId,
        Long userId,
        IntakeStatus intakeStatus,
        Integer lateMinutes
) {}
