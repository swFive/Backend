package com.example.medicineReminder.web.dto.notify;


import jakarta.validation.constraints.*;

public record NotificationTokenRequest(
        @NotNull Long userId,
        @NotBlank String fcmToken
) {}


