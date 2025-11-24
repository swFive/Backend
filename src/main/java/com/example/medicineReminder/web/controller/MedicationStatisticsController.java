package com.example.medicineReminder.web.controller;

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

    /**
     * 💡 [수정됨] Principal 처리 메서드
     * JwtTokenProvider가 'String ID'를 principal로 넣어주고 있으므로,
     * Object로 받아서 String으로 변환 후 Long으로 파싱해야 합니다.
     */
    private Long getUserId(Object principal) {
        if (principal == null) {
            throw new IllegalArgumentException("인증된 사용자 정보가 없습니다. (로그인 필요)");
        }

        // 1. 만약 나중에 Provider를 고쳐서 PrincipalDetails를 넘기게 될 경우를 대비
        if (principal instanceof PrincipalDetails) {
            return ((PrincipalDetails) principal).getUser().getId();
        }

        // 2. 현재 상황: Principal이 단순 String ID ("4") 인 경우
        try {
            return Long.parseLong(principal.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID 형식입니다.");
        }
    }

    // 1. 일별 복용 통계 조회
    @GetMapping("/daily-intake")
    public ResponseEntity<List<MedicationStatisticsDto>> getDailyStatistics(
            @AuthenticationPrincipal Object principal // 💡 타입을 PrincipalDetails -> Object로 변경
    ) {
        return ResponseEntity.ok(
                statisticsService.getMedicationStatisticsByDuration(
                        getUserId(principal), // 💡 수정된 getUserId 호출
                        MedicationStatisticsService.DurationType.DAILY
                )
        );
    }

    // 2. 기간별(주간/월간) 통계 조회
    @GetMapping
    public ResponseEntity<List<MedicationStatisticsDto>> getStatisticsByDuration(
            @AuthenticationPrincipal Object principal, // 💡 Object로 변경
            @RequestParam(defaultValue = "DAILY") MedicationStatisticsService.DurationType duration
    ) {
        return ResponseEntity.ok(
                statisticsService.getMedicationStatisticsByDuration(
                        getUserId(principal),
                        duration
                )
        );
    }

    // 3. 고정 기간 (년/월) 복용 통계 조회
    @GetMapping("/fixed")
    public ResponseEntity<List<MedicationStatisticsDto>> getStatisticsByFixedPeriod(
            @AuthenticationPrincipal Object principal, // 💡 Object로 변경
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam(required = false) Integer week
    ) {
        return ResponseEntity.ok(
                statisticsService.getMedicationStatisticsByYearMonthWeek(
                        getUserId(principal),
                        year,
                        month,
                        week
                )
        );
    }
}