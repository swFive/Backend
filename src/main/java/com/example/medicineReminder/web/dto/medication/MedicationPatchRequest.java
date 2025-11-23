package com.example.medicineReminder.web.dto.medication;

public record MedicationPatchRequest(
        Integer currentQuantity, // null이면 미수정
        Integer doseUnitQuantity, // null이면 미수정
        Integer refillThreshold // null이면 미수정
) {}


