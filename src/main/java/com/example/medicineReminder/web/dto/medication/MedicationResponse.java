package com.example.medicineReminder.web.dto.medication;

import com.example.medicineReminder.domain.entity.UserMedications;

public record MedicationResponse(
        Long medicationId,
        Long userId,
        String name,
        String category,
        Boolean isPublic,
        Integer initialQuantity,
        Integer currentQuantity,
        Integer doseUnitQuantity,
        Integer refillThreshold
)
{
    public static MedicationResponse from(UserMedications m)
    {
        return new MedicationResponse(
                m.getMedicationId(),
                getFieldLong(m, "userId"),
                getFieldString(m, "name"),
                getFieldString(m, "category"),
                getFieldBoolean(m, "isPublic"),
                getFieldInteger(m, "initialQuantity"),
                getFieldInteger(m, "currentQuantity"),
                getFieldInteger(m, "doseUnitQuantity"),
                getFieldInteger(m, "refillThreshold")
        );
    }

    private static Long getFieldLong(Object o, String name)
    {
        try { var f = o.getClass().getDeclaredField(name); f.setAccessible(true); return (Long) f.get(o); }
        catch (Exception e) { return null; }
    }
    private static String getFieldString(Object o, String name)
    {
        try { var f = o.getClass().getDeclaredField(name); f.setAccessible(true); return (String) f.get(o); }
        catch (Exception e) { return null; }
    }
    private static Boolean getFieldBoolean(Object o, String name)
    {
        try { var f = o.getClass().getDeclaredField(name); f.setAccessible(true); return (Boolean) f.get(o); }
        catch (Exception e) { return null; }
    }
    private static Integer getFieldInteger(Object o, String name)
    {
        try { var f = o.getClass().getDeclaredField(name); f.setAccessible(true); return (Integer) f.get(o); }
        catch (Exception e) { return null; }
    }
}
