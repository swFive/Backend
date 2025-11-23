package com.example.medicineReminder.web.dto.medication;

import jakarta.validation.constraints.*;

public record MedicationCreateRequest(
        @NotNull Long userId,
        @NotBlank String name,
        String category,
        boolean isPublic,
        @PositiveOrZero Integer initialQuantity,
        @PositiveOrZero Integer currentQuantity,
        @Positive Integer doseUnitQuantity,
        @PositiveOrZero Integer refillThreshold
) {}
