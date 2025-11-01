package com.example.medicineReminder.web.dto.notify;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record NotificationSettingRequest(
        @NotNull Long userId,
        // 음수(사전 알림) 허용: -1440 ~ +1440
        @NotNull @Min(-1440) @Max(1440) Integer notifyTimeOffset,
        @NotNull Boolean isRepeat,
        @PositiveOrZero Integer reNotifyInterval
) {}
