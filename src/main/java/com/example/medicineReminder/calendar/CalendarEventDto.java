package com.example.medicineReminder.calendar;

import com.example.medicineReminder.medication_log.MedicationIntakeLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "캘린더의 각 날짜에 표시될 복용 이벤트 정보")
@Data
@Builder
public class CalendarEventDto {
    @Schema(description = "스케줄 고유 ID")
    private Long scheduleId;

    @Schema(description = "약 고유 ID")
    private Long medicationId;

    @Schema(description = "약 이름")
    private String medicationName;

    @Schema(description = "복용 시간", type = "string")
    private String intakeTime;

    @Schema(description = "복용 상태 (TAKEN, SKIPPED, LATE, 또는 null)")
    private MedicationIntakeLog.IntakeStatus status;
}