package com.example.medicineReminder.calendar;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
@SecurityRequirement(name = "bearerAuth")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    /**
     * (수정됨) 현재 인증된 사용자의 월별 캘린더 데이터를 조회합니다.
     * @param year 조회할 연도 (없으면 현재 연도)
     * @param month 조회할 월 (없으면 현재 월)
     * @return 날짜를 key로, 그날의 복용 이벤트 리스트를 value로 갖는 Map 데이터
     */
    @GetMapping
    public Map<LocalDate, List<CalendarEventDto>> getMonthlyData(
            // === 1. 보안을 위해 @RequestParam Long userId 제거 ===
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        LocalDate today = LocalDate.now();
        int currentYear = (year == null) ? today.getYear() : year;
        int currentMonth = (month == null) ? today.getMonthValue() : month;

        // === 2. 서비스가 스스로 사용자를 찾도록 변경 (userId 파라미터 제거) ===
        return calendarService.getMonthlySchedules(currentYear, currentMonth);
    }
}