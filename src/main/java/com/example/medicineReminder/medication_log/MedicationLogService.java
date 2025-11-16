package com.example.medicineReminder.medication_log;

import com.example.medicineReminder.mediinfo.IntakeSchedule;
import com.example.medicineReminder.mediinfo.IntakeScheduleRepository;
import com.example.medicineReminder.mediinfo.UserMedicationRepository; // (필요시)
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicationLogService {

    private final MedicationLogRepository logRepository;
    private final IntakeScheduleRepository scheduleRepository;

    public MedicationLogService(MedicationLogRepository logRepository,
                                IntakeScheduleRepository scheduleRepository) {
        this.logRepository = logRepository;
        this.scheduleRepository = scheduleRepository;
    }

    private Long getCurrentUserId() { return 1L; }

    // === [핵심 수정] 1단계: 단순 복용 기록 생성 메서드 복구 ===
    @Transactional
    public MedicationIntakeLog recordSimpleIntake(MedicationLogDto logDto) {
        Long scheduleId = logDto.getScheduleId();
        IntakeSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("스케줄 없음"));

        // (오늘 날짜 검증 로직 생략 가능 또는 유지)

        MedicationIntakeLog newLog = new MedicationIntakeLog();
        newLog.setScheduleId(scheduleId);
        newLog.setUserId(getCurrentUserId());
        newLog.setIntakeStatus(logDto.getIntakeStatus());

        // 수동 시간 처리
        if (logDto.getRecordTime() != null) {
            newLog.setRecordTime(logDto.getRecordTime());
        } else {
            newLog.setRecordTime(LocalDateTime.now());
        }

        return logRepository.save(newLog);
    }

    // (로그 조회, 삭제 메서드 기존 유지)
    public List<MedicationIntakeLog> getLogsForMedication(Long medicationId) { /*...*/ return List.of(); }
    @Transactional
    public void deleteLog(Long logId) { logRepository.deleteById(logId); }
}