package com.example.medicineReminder.mediinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "약 기본 정보 수정 요청 DTO")
@Data
public class MedicationUpdateDto {

    @NotBlank(message = "약 이름은 필수입니다.")
    @Schema(description = "약 이름", example = "타이레놀") // [수정] 예시 추가
    private String name;

    @NotBlank(message = "카테고리는 필수입니다.")
    @Schema(description = "카테고리", example = "진통제") // [수정] 예시 추가
    private String category;

    @Schema(description = "메모", example = "식후 30분") // [수정] 예시 추가
    private String memo;

    @Schema(description = "1회 복용량", example = "1") // [수정] 예시 추가
    private Integer doseUnitQuantity;

    @Schema(description = "현재 재고", example = "10") // [수정] 예시 추가
    private Integer currentQuantity;

    @Schema(description = "리필 알림 임계치", example = "3") // [수정] 예시 추가
    private Integer refillThreshold;
}