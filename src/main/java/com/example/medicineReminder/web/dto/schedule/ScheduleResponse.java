package com.example.medicineReminder.web.dto.schedule;

import com.example.medicineReminder.domain.entity.IntakeSchedules;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleResponse(
        Long scheduleId,
        Long medicationId,
        Long userId,
        LocalTime intakeTime,
        String frequency,
        LocalDate startDate,
        LocalDate endDate,
        Integer leadMinutes,
        Boolean isRepeat,
        Boolean reNotifyEnabled,
        Integer reNotifyIntervalMin,
        Boolean isActive
)
{
    public static ScheduleResponse from(IntakeSchedules s)
    {
        return new ScheduleResponse(
                s.getScheduleId(),
                // 엔티티에 게터가 없으면 엔티티에 최소 게터 추가 필요
                // 아래 값들이 null이면 엔티티에 게터 보강하세요
                // 여기서는 필수 필드만 사용
                // medicationId와 userId는 세터만 있고 게터가 없다면 게터를 추가하세요.
                getFieldLong(s, "medicationId"),
                getFieldLong(s, "userId"),
                s.getIntakeTime(),
                getFieldString(s, "frequency"),
                getFieldLocalDate(s, "startDate"),
                getFieldLocalDate(s, "endDate"),
                getFieldInteger(s, "leadMinutes"),
                getFieldBoolean(s, "isRepeat"),
                getFieldBoolean(s, "reNotifyEnabled"),
                getFieldInteger(s, "reNotifyIntervalMin"),
                s.getIsActive()
        );
    }

    private static Long getFieldLong(Object o, String name)
    {
        try {
            var f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return (Long) f.get(o);
        } catch (Exception e) { return null; }
    }
    private static String getFieldString(Object o, String name)
    {
        try {
            var f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return (String) f.get(o);
        } catch (Exception e) { return null; }
    }
    private static Integer getFieldInteger(Object o, String name)
    {
        try {
            var f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return (Integer) f.get(o);
        } catch (Exception e) { return null; }
    }
    private static Boolean getFieldBoolean(Object o, String name)
    {
        try {
            var f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return (Boolean) f.get(o);
        } catch (Exception e) { return null; }
    }
    private static LocalDate getFieldLocalDate(Object o, String name)
    {
        try {
            var f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return (LocalDate) f.get(o);
        } catch (Exception e) { return null; }
    }
}
