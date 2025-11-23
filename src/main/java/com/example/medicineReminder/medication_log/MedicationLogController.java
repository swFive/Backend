package com.example.medicineReminder.medication_log;

import com.example.medicineReminder.IntakeJournal.IntakeJournalService;
import com.example.medicineReminder.IntakeJournal.JournalCreateDto;
import com.example.medicineReminder.IntakeJournal.IntakeJournal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class MedicationLogController {

    private final MedicationLogService logService;
    private final IntakeJournalService journalService;

    public MedicationLogController(MedicationLogService logService, IntakeJournalService journalService) {
        this.logService = logService;
        this.journalService = journalService;
    }

    // === 1. 단순 복용 기록 (개별) ===
    @PostMapping("/intake")
    public ResponseEntity<MedicationIntakeLog> recordSimpleIntake(@Valid @RequestBody MedicationLogDto logDto) {
        MedicationIntakeLog savedLog = logService.recordSimpleIntake(logDto);
        return new ResponseEntity<>(savedLog, HttpStatus.CREATED);
    }

    // === 2. 복용 일지 작성 (그룹 묶기) ===
    @PostMapping("/journal")
    public ResponseEntity<IntakeJournal> createJournal(@Valid @RequestBody JournalCreateDto journalDto) {
        IntakeJournal savedJournal = journalService.createJournal(journalDto);
        return new ResponseEntity<>(savedJournal, HttpStatus.CREATED);
    }

    // (기타 조회/삭제 API 유지)
    @GetMapping("/medication/{medicationId}")
    public List<MedicationIntakeLog> getLogsForMedication(@PathVariable Long medicationId) {
        return logService.getLogsForMedication(medicationId);
    }

    @DeleteMapping("/{logId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIntakeLog(@PathVariable Long logId) {
        logService.deleteLog(logId);
    }
}