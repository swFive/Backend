package com.example.medicineReminder.web.dto.schedule;

public record SchedulePatchRequest(
        String intakeTime,          // "HH:mm"
        String frequency,           // 예: "매일"
        String startDate,           // "yyyy-MM-dd"
        String endDate,             // nullable 허용
        Integer leadMinutes,
        Boolean isRepeat,
        Boolean reNotifyEnabled,
        Integer reNotifyIntervalMin,
        Boolean isActive
) {}
