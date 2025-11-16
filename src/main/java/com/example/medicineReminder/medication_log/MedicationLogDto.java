package com.example.medicineReminder.medication_log;

import com.example.medicineReminder.medication_log.MedicationIntakeLog.IntakeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "단순 복용 기록 생성 요청 DTO")
@Data
public class MedicationLogDto {
    @Schema(description = "스케줄 ID", example = "1")
    @NotNull
    private Long scheduleId;

    @Schema(description = "복용 상태", example = "TAKEN")
    @NotNull
    private IntakeStatus intakeStatus;

    @Schema(description = "실제 섭취 시각 (수동 입력, null이면 현재시간)")
    private LocalDateTime recordTime;
}