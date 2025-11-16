package com.example.medicineReminder.mediinfo;

import com.example.medicineReminder.medication_log.MedicationIntakeLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "스케줄 및 로그 상태 정보")
@Data
public class ScheduleLogStatusDto {
    @Schema(description = "스케줄 고유 ID")
    private Long scheduleId;

    @Schema(description = "복용 시간", type = "string")
    private LocalTime intakeTime;

    @Schema(description = "복용 주기")
    private String frequency;

    @Schema(description = "복용 시작일", type = "string", format = "date")
    private LocalDate startDate;

    @Schema(description = "복용 종료일", type = "string", format = "date")
    private LocalDate endDate;

    @Schema(description = "오늘의 복용 기록 ID (기록이 없으면 null)")
    private Long logId;

    @Schema(description = "오늘의 복용 상태 (기록이 없으면 null)")
    private MedicationIntakeLog.IntakeStatus intakeStatus;

    @Schema(description = "오늘의 복용 기록 시간 (기록이 없으면 null)")
    private String recordTime;
}