package com.example.medicineReminder.web.controller;

import com.example.medicineReminder.web.dto.Statistics.MedicationStatisticsDto;
import com.example.medicineReminder.service.MedicationStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class MedicationStatisticsController {

    private final MedicationStatisticsService statisticsService;

    // 🚨 테스트용: 실제 환경에서는 인증 토큰에서 userId를 가져와야 합니다.
    // 현재는 @RequestParam으로 클라이언트에서 직접 받습니다.

    @GetMapping("/daily-intake")
    public ResponseEntity<List<MedicationStatisticsDto>> getDailyStatistics(
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                statisticsService.getMedicationStatisticsByDuration(
                        userId, MedicationStatisticsService.DurationType.DAILY
                )
        );
    }


    @GetMapping // /api/v1/statistics?duration=WEEKLY
    public ResponseEntity<List<MedicationStatisticsDto>> getStatisticsByDuration(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "DAILY") MedicationStatisticsService.DurationType duration) {

        return ResponseEntity.ok(
                statisticsService.getMedicationStatisticsByDuration(userId, duration)
        );
    }


    @GetMapping("/fixed")
    public ResponseEntity<List<MedicationStatisticsDto>> getStatisticsByFixedPeriod(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {

        YearMonth ym = YearMonth.of(year, month);
        LocalDate startDate = ym.atDay(1);                // 예: 2025-11-01
        LocalDate endDate = ym.plusMonths(1).atDay(1);    // 예: 2025-12-01

        return ResponseEntity.ok(
                statisticsService.getMedicationStatisticsByYearMonthWeek(
                        userId,
                        year,
                        month,
                        null // 💡 week 파라미터에 null을 전달하여 월 전체 조회
                )
        );
    }
}