package com.example.medicineReminder.IntakeJournal;

// === [핵심 수정] IntakeStatus를 올바른 경로에서 import ===
import com.example.medicineReminder.medication_log.MedicationIntakeLog.IntakeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "복용 일지(그룹) 생성 요청 DTO")
@Data
public class JournalCreateDto {

    @Schema(description = "일지에 묶을 복용 기록(Log) ID 목록")
    @NotEmpty
    private List<Long> logIds;


    @Schema(description = "일지 작성 시각")
    @NotNull
    private LocalDateTime journalTime;

    @Schema(description = "컨디션 이모지")
    private String conditionEmoji;

    @Schema(description = "복용 후 메모")
    private String logMemo;
}