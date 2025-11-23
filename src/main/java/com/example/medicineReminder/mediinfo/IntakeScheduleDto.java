package com.example.medicineReminder.mediinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "스케줄 수정 요청 DTO")
@Data
public class IntakeScheduleDto {
    @Schema(description = "복용 시간", type = "string", example = "09:30")
    private LocalTime intakeTime;

    @Schema(description = "복용 주기", example = "매일")
    private String frequency;

    @Schema(description = "복용 시작일", type = "string", format = "date", example = "2025-10-01")
    private LocalDate startDate;

    @Schema(description = "복용 종료일", type = "string", format = "date", example = "2025-12-31")
    private LocalDate endDate;
}