package com.example.medicineReminder.IntakeJournal;

import com.example.medicineReminder.medication_log.MedicationIntakeLog.IntakeStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalTime;

@Data
@Builder
public class JournalLogDetailDto {
    private Long logId;
    private String medicationName;
    private LocalTime intakeTime; // (예정 시간)
    private IntakeStatus intakeStatus;
}