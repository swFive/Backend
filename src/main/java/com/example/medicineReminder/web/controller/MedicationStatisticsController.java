package com.example.medicineReminder.web.controller;

// 👇 기존에 보내주신 PrincipalDetails 클래스 경로
import com.example.medicineReminder.domain.PrincipalDetails;
import com.example.medicineReminder.web.dto.Statistics.MedicationStatisticsDto;
import com.example.medicineReminder.service.MedicationStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class MedicationStatisticsController {

    private final MedicationStatisticsService statisticsService;

    // ✅ 공통 편의 메서드: PrincipalDetails에서 user_id 추출
    private Long getUserId(PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            throw new IllegalArgumentException("인증된 사용자 정보가 없습니다. (로그인 필요)");
        }
        // PrincipalDetails -> AppUsers -> getId() (DB의 PK)
        return principalDetails.getUser().getId();
    }

    // 1. 일별 복용 통계 조회
    @GetMapping("/daily-intake")
    public ResponseEntity<List<MedicationStatisticsDto>> getDailyStatistics(
            @AuthenticationPrincipal PrincipalDetails principalDetails // 🔐 토큰 자동 주입
    ) {
        return ResponseEntity.ok(
                statisticsService.getMedicationStatisticsByDuration(
                        getUserId(principalDetails),
                        MedicationStatisticsService.DurationType.DAILY
                )
        );
    }

    // 2. 기간별(주간/월간) 통계 조회
    @GetMapping
    public ResponseEntity<List<MedicationStatisticsDto>> getStatisticsByDuration(
            @AuthenticationPrincipal PrincipalDetails principalDetails, // 🔐 토큰 자동 주입
            @RequestParam(defaultValue = "DAILY") MedicationStatisticsService.DurationType duration
    ) {
        return ResponseEntity.ok(
                statisticsService.getMedicationStatisticsByDuration(
                        getUserId(principalDetails),
                        duration
                )
        );
    }

    // 3. 고정 기간 (년/월) 복용 통계 조회
    @GetMapping("/fixed")
    public ResponseEntity<List<MedicationStatisticsDto>> getStatisticsByFixedPeriod(
            @AuthenticationPrincipal PrincipalDetails principalDetails, // 🔐 토큰 자동 주입
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam(required = false) Integer week
    ) {
        // 날짜 계산은 서비스 계층에서 처리하므로 바로 호출
        return ResponseEntity.ok(
                statisticsService.getMedicationStatisticsByYearMonthWeek(
                        getUserId(principalDetails),
                        year,
                        month,
                        week
                )
        );
    }
}