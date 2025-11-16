package com.example.medicineReminder.IntakeJournal;

import com.example.medicineReminder.medication_log.MedicationIntakeLog;
import com.example.medicineReminder.medication_log.MedicationLogRepository;
import com.example.medicineReminder.mediinfo.AppUser;
import com.example.medicineReminder.mediinfo.AppUserRepository;
import com.example.medicineReminder.mediinfo.IntakeSchedule; // [추가]
import com.example.medicineReminder.mediinfo.IntakeScheduleRepository; // [추가]
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime; // [추가]
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // [추가]

@Service
public class IntakeJournalService {

    private final IntakeJournalRepository journalRepository;
    private final MedicationLogRepository logRepository;
    private final AppUserRepository userRepository;
    private final IntakeScheduleRepository scheduleRepository; // [추가]

    public IntakeJournalService(IntakeJournalRepository journalRepository,
                                MedicationLogRepository logRepository,
                                AppUserRepository userRepository,
                                IntakeScheduleRepository scheduleRepository) { // [추가]
        this.journalRepository = journalRepository;
        this.logRepository = logRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository; // [추가]
    }

    private Long getCurrentUserId() { return 1L; }

    @Transactional
    public IntakeJournal createJournal(JournalCreateDto journalDto) {
        Long currentUserId = getCurrentUserId();
        AppUser currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        IntakeJournal journal = new IntakeJournal();
        journal.setUser(currentUser);
        journal.setJournalTime(journalDto.getJournalTime());
        journal.setConditionEmoji(journalDto.getConditionEmoji());
        journal.setLogMemo(journalDto.getLogMemo());

        IntakeJournal savedJournal = journalRepository.save(journal);

        List<MedicationIntakeLog> logsToUpdate = new ArrayList<>();
        for (Long logId : journalDto.getLogIds()) {
            MedicationIntakeLog log = logRepository.findById(logId)
                    .orElseThrow(() -> new RuntimeException("로그 ID 없음: " + logId));

            if (!log.getUserId().equals(currentUserId)) {
                throw new RuntimeException("권한 없음");
            }
            if (log.getJournal() != null) {
                throw new RuntimeException("이미 다른 일지에 포함된 기록입니다 - 로그 ID: " + log.getId());
            }

            // (개별 로그에 이미 TAKE/SKIPPED 상태가 있으므로 DTO에서 설정할 필요 없음)
            // log.setIntakeStatus(journalDto.getIntakeStatus());

            log.setJournal(savedJournal);
            logsToUpdate.add(log);
        }

        logRepository.saveAll(logsToUpdate);

        savedJournal.setLogs(logsToUpdate);
        return savedJournal;
    }

    /**
     * === [신규 추가] 2. 일지 목록 조회 (GET /api/journals) ===
     */
    @Transactional(readOnly = true)
    public List<JournalViewDto> getJournalsForUser() {
        Long currentUserId = getCurrentUserId();
        List<IntakeJournal> journals = journalRepository.findByUserIdOrderByJournalTimeDesc(currentUserId);

        return journals.stream()
                .map(this::mapJournalToViewDto)
                .collect(Collectors.toList());
    }

    /**
     * === [신규 추가] 헬퍼: Journal -> JournalViewDto 변환 ===
     */
    private JournalViewDto mapJournalToViewDto(IntakeJournal journal) {

        List<JournalLogDetailDto> logDetails = journal.getLogs().stream()
                .map(log -> {
                    IntakeSchedule schedule = scheduleRepository.findById(log.getScheduleId())
                            .orElse(null);

                    String medName = (schedule != null && schedule.getMedication() != null) ? schedule.getMedication().getName() : "[알 수 없음]";
                    LocalTime intakeTime = (schedule != null) ? schedule.getIntakeTime() : LocalTime.MIDNIGHT;

                    return JournalLogDetailDto.builder()
                            .logId(log.getId())
                            .medicationName(medName)
                            .intakeTime(intakeTime)
                            .intakeStatus(log.getIntakeStatus())
                            .build();
                })
                .sorted(java.util.Comparator.comparing(JournalLogDetailDto::getIntakeTime))
                .collect(Collectors.toList());

        return JournalViewDto.builder()
                .journalId(journal.getId())
                .journalTime(journal.getJournalTime())
                .conditionEmoji(journal.getConditionEmoji())
                .logMemo(journal.getLogMemo())
                .logs(logDetails)
                .build();
    }
}