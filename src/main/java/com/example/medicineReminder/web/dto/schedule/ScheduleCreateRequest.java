package com.example.medicineReminder.web.dto.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScheduleCreateRequest(
        @NotNull Long medicationId,
        @NotNull Long userId,
        @NotBlank String intakeTime,     // "HH:mm"
        @NotBlank String frequency,      // 예: "매일"
        @NotBlank String startDate,      // "yyyy-MM-dd"
        String endDate,                  // nullable
        Integer leadMinutes,             // nullable → null이면 0으로 처리
        Boolean isRepeat,                // nullable → null이면 true
        Boolean reNotifyEnabled,         // nullable → null이면 true
        Integer reNotifyIntervalMin      // nullable → null이면 5
) {}
