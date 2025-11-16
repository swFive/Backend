package com.example.medicineReminder.mediinfo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules") // 스케줄 관련 API는 이 주소를 사용합니다.
public class IntakeScheduleController {

    private final IntakeScheduleService scheduleService;

    public IntakeScheduleController(IntakeScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * 특정 스케줄의 정보를 수정합니다.
     * @param scheduleId 수정할 스케줄의 ID
     * @param dto 수정할 내용이 담긴 데이터
     * @return 수정된 스케줄 정보
     */
    @PutMapping("/{scheduleId}")
    public IntakeSchedule update(@PathVariable Long scheduleId, @RequestBody IntakeScheduleDto dto) {
        return scheduleService.updateSchedule(scheduleId, dto);
    }
    /**
     * 특정 스케줄을 삭제합니다.
     * @param scheduleId 삭제할 스케줄의 ID
     */
    @DeleteMapping("/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 성공 시 204 No Content 응답을 보냅니다.
    public void delete(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
    }
}