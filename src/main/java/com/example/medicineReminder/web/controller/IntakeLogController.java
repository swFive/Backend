// src/main/java/com/example/medicineReminder/web/controller/IntakeLogController.java
package com.example.medicineReminder.web.controller;

import com.example.medicineReminder.domain.entity.MedicationIntakeLogs;
import com.example.medicineReminder.service.IntakeLogService;
import com.example.medicineReminder.web.dto.intake.IntakeLogCreateRequest;
import com.example.medicineReminder.web.dto.intake.IntakeLogResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/intake-logs")
public class IntakeLogController
{
    private final IntakeLogService service;

    public IntakeLogController(IntakeLogService service)
    {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<IntakeLogResponse> create(@RequestBody @Valid IntakeLogCreateRequest req)
    {
        MedicationIntakeLogs saved = service.create(
                req.scheduleId(), req.userId(), req.intakeStatus(), req.lateMinutes()
        );

        URI location = URI.create("/api/intake-logs/" + saved.getLogId());
        return ResponseEntity.created(location).body(IntakeLogResponse.from(saved));
    }
}
