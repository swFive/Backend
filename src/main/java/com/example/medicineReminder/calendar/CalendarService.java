package com.example.medicineReminder.calendar;

import com.example.medicineReminder.mediinfo.IntakeSchedule;
import com.example.medicineReminder.mediinfo.UserMedication;
import com.example.medicineReminder.mediinfo.UserMedicationRepository;
import com.example.medicineReminder.medication_log.MedicationIntakeLog;
import com.example.medicineReminder.medication_log.MedicationLogRepository;
import com.example.medicineReminder.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    private final UserMedicationRepository medicationRepository;
    private final MedicationLogRepository logRepository;

    public CalendarService(UserMedicationRepository medicationRepository, MedicationLogRepository logRepository) {
        this.medicationRepository = medicationRepository;
        this.logRepository = logRepository;
    }



    // === 1. 메서드 시그니처에서 userId 파라미터 제거 ===
    public Map<LocalDate, List<CalendarEventDto>> getMonthlySchedules(int year, int month) {

        // === 2. 내부에서 현재 사용자 ID를 직접 가져옴 ===
        Long currentUserId = SecurityUtil.getCurrentUserId();
        List<UserMedication> medications = medicationRepository.findByUserId(currentUserId);
        if (medications.isEmpty()) {
            return new HashMap<>();
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDateOfMonth = yearMonth.atDay(1);
        LocalDate endDateOfMonth = yearMonth.atEndOfMonth();

        List<Long> allScheduleIds = medications.stream()
                .flatMap(med -> med.getSchedules().stream().map(IntakeSchedule::getId))
                .collect(Collectors.toList());

        if (allScheduleIds.isEmpty()) return new HashMap<>();

        // 월의 시작~끝에 해당하는 로그를 모두 조회
        List<MedicationIntakeLog> logs = logRepository.findLogsByScheduleIdsAndDate(
                allScheduleIds, startDateOfMonth.atStartOfDay(), endDateOfMonth.plusDays(1).atStartOfDay());

        // (날짜 + 스케줄ID)를 Key로, 복용 상태(Status)를 Value로 갖는 Map 생성
        Map<String, MedicationIntakeLog.IntakeStatus> logStatusMap = logs.stream()
                .collect(Collectors.toMap(
                        // (날짜 + 스케줄ID)를 Key로 사용
                        log -> log.getRecordTime().toLocalDate().toString() + "_" + log.getScheduleId(),
                        MedicationIntakeLog::getIntakeStatus
                ));

        Map<LocalDate, List<CalendarEventDto>> monthlyEvents = new HashMap<>();

        // 1일부터 마지막 날까지 반복
        for (LocalDate date = startDateOfMonth; !date.isAfter(endDateOfMonth); date = date.plusDays(1)) {
            final LocalDate currentDate = date;
            List<CalendarEventDto> dailyEvents = new ArrayList<>();

            for (UserMedication med : medications) {
                for (IntakeSchedule sc : med.getSchedules()) {
                    // 1. 오늘 복용하는 스케줄이 맞는지 확인
                    if (isScheduleActiveOn(sc, currentDate)) {
                        String key = currentDate.toString() + "_" + sc.getId();

                        // === 3. [로직 오류 수정] ===
                        // logStatusMap.get(key)를 호출.
                        // 로그가 없으면(logStatusMap.get(key) == null) 'LATE'가 아닌 'null' (미복용/예정) 상태가 됩니다.
                        MedicationIntakeLog.IntakeStatus status = logStatusMap.get(key);

                        dailyEvents.add(CalendarEventDto.builder()
                                .scheduleId(sc.getId())
                                .medicationId(med.getId())
                                .medicationName(med.getName())
                                .intakeTime(sc.getIntakeTime().toString())
                                .status(status) // 로그가 없으면 status는 null
                                .build());
                    }
                }
            }
            if (!dailyEvents.isEmpty()) {
                // 시간순으로 정렬
                dailyEvents.sort(Comparator.comparing(CalendarEventDto::getIntakeTime));
                monthlyEvents.put(currentDate, dailyEvents);
            }
        }
        return monthlyEvents;
    }

    /**
     * 특정 날짜에 해당 스케줄이 유효한지(복용일이 맞는지) 확인하는 헬퍼 메서드
     * (참고: MedicationLogService에도 동일한 메서드가 있으므로,
     * 향후 Util 클래스로 분리하는 것을 권장합니다.)
     */
    private boolean isScheduleActiveOn(IntakeSchedule schedule, LocalDate date) {
        // 1. 기간 확인
        boolean inDateRange = !date.isBefore(schedule.getStartDate()) && (schedule.getEndDate() == null || !date.isAfter(schedule.getEndDate()));
        if (!inDateRange) return false;

        // 2. 주기(요일) 확인
        String frequency = schedule.getFrequency();
        if (frequency == null || frequency.trim().isEmpty()) return false;

        // "매일" 또는 "DAILY" (DB 스키마 정의에 따라)
        if (frequency.equals("매일") || frequency.equalsIgnoreCase("DAILY")) {
            return true;
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        String dayKorean = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN); // "월", "화", ...

        return frequency.contains(dayKorean);
    }
}