package com.example.medicineReminder.mediinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Schema(description = "약 정보 등록/수정 요청 DTO")
@Data
public class MedicationDto {
    @Schema(description = "카테고리 (예: 영양제, 처방약)", example = "영양제")
    @NotBlank(message = "카테고리는 필수 입력 값입니다.")
    private String category;

    @Schema(description = "약 이름", example = "비타민C")
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;


    @Schema(description = "메모", example = "식후 즉시 복용")
    private String memo; // (DB에 memo 컬럼은 존재하므로 남겨둡니다)

    @Schema(description = "복용 시간 (쉼표로 구분)", example = "09:00,18:00")
    @NotBlank(message = "복용 시간은 필수 입력 값입니다.")
    private String times;

    @Schema(description = "복용 주기 (요일, 쉼표로 구분)", example = "월,수,금")
    @NotBlank(message = "복용 주기는 필수 입력 값입니다.")
    private String days;

    @Schema(description = "복용 시작일", type = "string", format = "date", example = "2025-10-01")
    @NotNull(message = "시작일은 필수 입력 값입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String startDate;

    @Schema(description = "복용 종료일", type = "string", format = "date", example = "2025-12-31")
    @NotNull(message = "종료일은 필수 입력 값입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String endDate;

    @Schema(description = "1회 복용량(정)", example = "1")
    @NotNull(message = "1회 복용량은 필수입니다.")
    @Min(value = 0, message = "1회 복용량은 0 이상이어야 합니다.")
    private Integer doseUnitQuantity;

    @Schema(description = "총 재고(정)", example = "60")
    @NotNull(message = "총 재고는 필수입니다.")
    @Min(value = 0, message = "총 재고는 0 이상이어야 합니다.")
    private Integer initialQuantity;
}