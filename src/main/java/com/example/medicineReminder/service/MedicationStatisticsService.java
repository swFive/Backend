package com.example.medicineReminder.service;

import com.example.medicineReminder.web.dto.Statistics.MedicationStatisticsDto;
import com.example.medicineReminder.web.dto.Statistics.MedicationFailureDto;
import com.example.medicineReminder.medication_log.MedicationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Slf4j//테스트
@Service
@RequiredArgsConstructor
public class MedicationStatisticsService {

    private final MedicationLogRepository medicationLogRepository;

    // 💡 [추가] 통계 유효성을 위한 최소 기록 횟수 정의 (5회 미만이면 유효하지 않다고 판단)
    private static final int MIN_RECORDS_FOR_STATS = 1;

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

            // 실제 쿼리 결과 타입 확인 (로깅 목적 유지)
            List<Object[]> rawResults = medicationLogRepository.findAggregateStatisticsRaw(
                    userId, startDateTime, endDateTime
            );
            // ... (쿼리 결과 로깅 로직 유지)
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
            List<MedicationStatisticsDto> statsList = medicationLogRepository.findAggregateStatisticsByDateRange(userId, startDateTime, endDateTime);

            // 💡 [추가] 통계 유효성 검사 (총 기록 횟수 체크)
            if (statsList.isEmpty()) {
                log.info("기간별 통계: 데이터 없음.");
                return statsList;
            }

            MedicationStatisticsDto aggregateStats = statsList.get(0);
            if (aggregateStats.getTotalRecords() < MIN_RECORDS_FOR_STATS) {
                log.warn("기간별 통계: 총 기록 횟수({})가 최소 기준({}) 미만이므로 유효하지 않음. 빈 리스트 반환.",
                        aggregateStats.getTotalRecords(), MIN_RECORDS_FOR_STATS);
                return Collections.emptyList();
            }

            return statsList;
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

        List<MedicationStatisticsDto> statsList = medicationLogRepository.findAggregateStatisticsByDateRange(userId, startDateTime, endDateTime);

        // 💡 [추가] 고정 기간 통계 유효성 검사
        if (statsList.isEmpty()) {
            log.info("고정 기간별 통계: 데이터 없음.");
            return statsList;
        }

        MedicationStatisticsDto aggregateStats = statsList.get(0);
        if (aggregateStats.getTotalRecords() < MIN_RECORDS_FOR_STATS) {
            log.warn("고정 기간별 통계: 총 기록 횟수({})가 최소 기준({}) 미만이므로 유효하지 않음. 빈 리스트 반환.",
                    aggregateStats.getTotalRecords(), MIN_RECORDS_FOR_STATS);
            return Collections.emptyList();
        }

        return statsList;
    }


    /**
     * 3. [추가] 미복용률이 높은 상위 N개 약물을 조회합니다. (TOP 3)
     * @param userId 현재 로그인한 사용자 ID
     * @return MedicationFailureDto 리스트 (약물명, 실패율 포함)
     */
    public List<MedicationFailureDto> getTopMissedMedications(Long userId) {

        // 1. Repository에서 약물별 미복용 통계 원본 조회
        List<Object[]> rawResults = medicationLogRepository.findTopMissedMedicationsRaw(userId);

        // 2. Object[] 결과를 MedicationFailureDto 리스트로 매핑
        if (rawResults.isEmpty()) {
            return Collections.emptyList();
        }

        return rawResults.stream().map(row -> new MedicationFailureDto(
                (String) row[0],        // medicationName (인덱스 0)
                (BigDecimal) row[1]     // failureRate (인덱스 1)
        )).collect(Collectors.toList());
    }
}