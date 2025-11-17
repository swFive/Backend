package com.example.medicineReminder.web.dto.Statistics;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;  // ⭐ java.sql.Date 사용
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class MedicationStatisticsDto {

    private String userName;
    private LocalDate date;
    private Long totalRecords;
    private Long successCount;
    private Long onTimeCount;
    private Long lateCount;
    private Long failureCount;
    private Long skippedCount;
    private Double successRate;
    private Double failureRate;

    // ✅ 실제 반환 타입에 맞춘 생성자
    public MedicationStatisticsDto(
            String userName,
            Date date,              // ⭐ java.sql.Date로 받기
            Long totalRecords,      // ⭐ Long으로 받기
            Long successCount,      // ⭐ Long으로 받기
            Long onTimeCount,       // ⭐ Long으로 받기
            Long lateCount,         // ⭐ Long으로 받기
            Long failureCount,      // ⭐ Long으로 받기
            Long skippedCount,      // ⭐ Long으로 받기
            BigDecimal successRate, // ⭐ BigDecimal로 받기
            BigDecimal failureRate  // ⭐ BigDecimal로 받기
    ) {
        this.userName = userName;
        // java.sql.Date를 LocalDate로 변환
        this.date = date != null ? date.toLocalDate() : null;
        // Long은 그대로 사용
        this.totalRecords = totalRecords != null ? totalRecords : 0L;
        this.successCount = successCount != null ? successCount : 0L;
        this.onTimeCount = onTimeCount != null ? onTimeCount : 0L;
        this.lateCount = lateCount != null ? lateCount : 0L;
        this.failureCount = failureCount != null ? failureCount : 0L;
        this.skippedCount = skippedCount != null ? skippedCount : 0L;
        // BigDecimal을 Double로 변환
        this.successRate = successRate != null ? successRate.doubleValue() : 0.0;
        this.failureRate = failureRate != null ? failureRate.doubleValue() : 0.0;
    }
}
