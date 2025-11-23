package com.example.medicineReminder.service;

import com.example.medicineReminder.web.dto.Statistics.MedicationStatisticsDto;
import com.example.medicineReminder.medication_log.MedicationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
@Slf4j//테스트
@Service
@RequiredArgsConstructor
public class MedicationStatisticsService {

    private final MedicationLogRepository medicationLogRepository;

    public enum DurationType {
        DAILY, WEEKLY, MONTHLY
    }

    /**
     * 1. 기간 유형 (DAILY, WEEKLY, MONTHLY)에 따른 통계를 조회합니다.
     * @param userId 현재 로그인한 사용자 ID
     * @param type 조회할 기간 유형 (WEEKLY/MONTHLY/DAILY)
     */
    public List<MedicationStatisticsDto> getMedicationStatisticsByDuration(Long userId, DurationType type) {

        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (type) {
            case WEEKLY:
                // 이번 주 월요일부터 오늘까지
                startDate = endDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                break;
            case MONTHLY:
                // 이번 달 1일부터 오늘까지
                startDate = endDate.with(TemporalAdjusters.firstDayOfMonth());
                break;
            case DAILY:
            default:
                startDate = endDate;
                break;
        }

        // 🚨 LocalDate를 LocalDateTime 경계로 변환 (타임존/경계 오류 방지)
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay(); // 다음 날 자정 (배타적 경계)

        if (type == DurationType.DAILY) {
            // DAILY는 일별 상세 통계를 반환
            return medicationLogRepository.findMedicationStatisticsByDateRange(userId, startDateTime, endDateTime);
        } else {

            log.info("=== 월별/주별 통계 조회 시작 ===");
            log.info("Duration Type: {}", type);
            log.info("Start DateTime: {}, End DateTime: {}", startDateTime, endDateTime);

            // 실제 쿼리 결과 타입 확인
            List<Object[]> rawResults = medicationLogRepository.findAggregateStatisticsRaw(
                    userId, startDateTime, endDateTime
            );

            if (!rawResults.isEmpty()) {
                Object[] row = rawResults.get(0);
                log.info("=== 쿼리 결과 타입 확인 ===");
                for (int i = 0; i < row.length; i++) {
                    String typeName = row[i] != null ? row[i].getClass().getName() : "null";
                    log.info("Column {}: Type = {}, Value = {}", i, typeName, row[i]);
                }
            } else {
                log.info("쿼리 결과가 비어있습니다.");
            }
            // WEEKLY, MONTHLY는 총합 통계를 반환
            return medicationLogRepository.findAggregateStatisticsByDateRange(userId, startDateTime, endDateTime);
        }
    }

    /**
     * 2. 특정 연월 또는 주차를 지정하여 통계를 조회합니다.
     * @param userId 현재 로그인한 사용자 ID
     */
    public List<MedicationStatisticsDto> getMedicationStatisticsByYearMonthWeek(Long userId, int year, int month, Integer week) {

        LocalDate startDate;
        LocalDate endDate;

        if (Objects.isNull(week)) {
            // 월 전체 조회 요청 (총합 통계를 반환)
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1);
            endDate = yearMonth.atEndOfMonth();
        } else {
            // 주차별 조회 요청 (총합 통계를 반환)
            LocalDate firstOfMonth = LocalDate.of(year, month, 1);

            LocalDate weekStartCandidate = firstOfMonth.plusWeeks(week - 1)
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

            startDate = weekStartCandidate.isBefore(firstOfMonth) ? firstOfMonth : weekStartCandidate;

            LocalDate weekEndCandidate = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            LocalDate endOfMonth = YearMonth.of(year, month).atEndOfMonth();
            endDate = weekEndCandidate.isAfter(endOfMonth) ? endOfMonth : weekEndCandidate;
        }

        // 🚨 LocalDate를 LocalDateTime 경계로 변환
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay(); // 종료일 다음 날 자정

        return medicationLogRepository.findAggregateStatisticsByDateRange(userId, startDateTime, endDateTime);
    }
}