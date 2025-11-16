package com.example.medicineReminder.mediinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "약 기본 정보 수정 요청 DTO")
@Data
public class MedicationUpdateDto {

    @NotBlank(message = "약 이름은 필수입니다.")
    @Schema(description = "약 이름")
    private String name;

    @NotBlank(message = "카테고리는 필수입니다.")
    @Schema(description = "카테고리")
    private String category;

    @Schema(description = "메모")
    private String memo; // DB에 memo 컬럼이 있으므로

    // (스크린샷 UI에 재고 수정 기능이 있으므로 추가)
    @Schema(description = "1회 복용량")
    private Integer doseUnitQuantity;

    @Schema(description = "현재 재고")
    private Integer currentQuantity;

    @Schema(description = "리필 알림 임계치")
    private Integer refillThreshold;

    // (참고: isPublic은 UI에서 제거했으므로 여기서는 뺌)
}