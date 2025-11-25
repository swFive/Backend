package com.example.medicineReminder.web.dto.Statistics;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
// BigDecimal은 SQL의 DECIMAL 또는 ROUND 함수 결과와 정확히 매핑하기 위해 사용합니다.

@Data
@NoArgsConstructor
public class MedicationFailureDto {

    private String medicationName;
    private Double failureRate; // 소수점 첫째 자리까지 표시할 최종 미복용률 (0.0 ~ 100.0)

    // 네이티브 쿼리 결과를 받아 필드에 매핑하는 생성자
    public MedicationFailureDto(
            String medicationName,
            BigDecimal failureRate
    ) {
        this.medicationName = medicationName;
        // BigDecimal을 Double로 변환 (프론트엔드에서 실수형을 기대)
        this.failureRate = failureRate != null ? failureRate.doubleValue() : 0.0;
    }
}