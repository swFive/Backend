package com.example.medicineReminder.mediinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Schema(description = "약 정보 및 로그 상태 응답 DTO")
@Data
public class MedicationWithLogsDto {
    @Schema(description = "약 고유 ID")
    private Long medicationId;

    @Schema(description = "약 이름")
    private String name;

    @Schema(description = "카테고리")
    private String category;

    // ▼▼▼ [추가된 필드] ▼▼▼
    @Schema(description = "메모")
    private String memo;

    @Schema(description = "현재 재고")
    private Integer currentQuantity;

    @Schema(description = "1회 복용량")
    private Integer doseUnitQuantity;
    // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲

    @Schema(description = "계산된 다음 복용 시간 (예: '다음: 오늘 18:00')")
    private String nextIntakeTime;

    @Schema(description = "스케줄 및 로그 정보 리스트")
    private List<ScheduleLogStatusDto> schedulesWithLogs;
}