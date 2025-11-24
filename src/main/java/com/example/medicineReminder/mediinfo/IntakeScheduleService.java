package com.example.medicineReminder.mediinfo;

import com.example.medicineReminder.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IntakeScheduleService {

    private final IntakeScheduleRepository scheduleRepository;

    public IntakeScheduleService(IntakeScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }



    @Transactional
    public IntakeSchedule updateSchedule(Long scheduleId, IntakeScheduleDto dto) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        // 1. ID로 스케줄을 찾습니다.
        IntakeSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 스케줄을 찾을 수 없습니다: " + scheduleId));

        // === 2. 권한 확인: 이 스케줄이 현재 사용자의 것인지 확인 ===
        if (!schedule.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("이 스케줄을 수정할 권한이 없습니다.");
        }

        // 3. DTO의 내용으로 스케줄 정보를 업데이트합니다.
        schedule.setIntakeTime(dto.getIntakeTime());
        schedule.setFrequency(dto.getFrequency());
        schedule.setStartDate(dto.getStartDate());
        schedule.setEndDate(dto.getEndDate());
        // (참고: DTO가 확장되면 leadMinutes 등 다른 필드도 여기서 업데이트)

        // 4. 변경된 내용을 저장합니다.
        return scheduleRepository.save(schedule);
    }

    @Transactional // 삭제 로직에도 @Transactional 추가 권장
    public void deleteSchedule(Long scheduleId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        // 1. ID로 스케줄을 찾습니다.
        IntakeSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 스케줄을 찾을 수 없습니다: " + scheduleId));

        // === 2. 권한 확인: 이 스케줄이 현재 사용자의 것인지 확인 ===
        if (!schedule.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("이 스케줄을 삭제할 권한이 없습니다.");
        }

        // 3. 권한 확인 후 삭제
        scheduleRepository.deleteById(scheduleId);
    }
}