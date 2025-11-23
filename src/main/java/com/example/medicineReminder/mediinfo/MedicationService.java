package com.example.medicineReminder.mediinfo;

import com.example.medicineReminder.domain.entity.AppUsers;
import com.example.medicineReminder.medication_log.MedicationIntakeLog;
import com.example.medicineReminder.medication_log.MedicationLogRepository;
import com.example.medicineReminder.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek; // (nextIntakeTime 계산용)
import java.time.LocalDate;
import java.time.LocalDateTime; // (nextIntakeTime 계산용)
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle; // (nextIntakeTime 계산용)
import java.util.*; // (nextIntakeTime 계산용)
import java.util.stream.Collectors;

@Service("mediInfoMedicationService") // 빈 이름을 명시적으로 지정해서 도메인쪽 MedicationService와 충돌 방지
public class MedicationService {
    private final UserMedicationRepository repo;
    private final MedicationLogRepository logRepo;
    private final UserRepository userRepo;

    public MedicationService(UserMedicationRepository repo, MedicationLogRepository logRepo, UserRepository userRepo) {
        this.repo = repo;
        this.logRepo = logRepo;
        this.userRepo = userRepo;
    }

    // (임시) 현재 사용자 ID를 가져오는 메서드
    private Long getCurrentUserId() {
        return 1L;
    }

    // 현재 사용자 AppUser 객체를 가져오는 메서드
    private AppUsers getCurrentUser() {
        Long currentUserId = getCurrentUserId();
        return userRepo.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("현재 사용자 정보를 찾을 수 없습니다: " + currentUserId));
    }

    // 1. 약 저장(save) 메서드
    @Transactional
    public UserMedication save(MedicationDto MedicationDto) {
        AppUsers currentUser = getCurrentUser();

        UserMedication medication = new UserMedication();
        medication.setUser(currentUser);

        medication.setCategory(MedicationDto.getCategory());
        medication.setName(MedicationDto.getName());

        // (DB 스키마에 frequency가 없으므로 주석 처리)
        // medication.setFrequency(dto.getFrequency());
        medication.setMemo(MedicationDto.getMemo()); // (DB에 memo는 있음)

        medication.setInitialQuantity(MedicationDto.getInitialQuantity());
        medication.setCurrentQuantity(MedicationDto.getInitialQuantity());
        medication.setDoseUnitQuantity(MedicationDto.getDoseUnitQuantity());

        List<IntakeSchedule> schedules = new ArrayList<>();
        LocalDate startDate = LocalDate.parse(MedicationDto.getStartDate());
        LocalDate endDate = LocalDate.parse(MedicationDto.getEndDate());
        String[] timesArray = MedicationDto.getTimes().split(",");

        for (String timeStr : timesArray) {
            if (timeStr != null && !timeStr.trim().isEmpty()) {
                IntakeSchedule schedule = new IntakeSchedule();
                schedule.setMedication(medication);
                schedule.setUser(currentUser);
                schedule.setIntakeTime(LocalTime.parse(timeStr.trim()));
                schedule.setFrequency(MedicationDto.getDays());
                schedule.setStartDate(startDate);
                schedule.setEndDate(endDate);

                schedules.add(schedule);
            }
        }
        medication.setSchedules(schedules);
        return repo.save(medication);
    }

    // === [핵심 수정] 2. 약 기본 정보 업데이트 서비스 로직 ===
    @Transactional
    public UserMedication updateMedication(Long medicationId, MedicationUpdateDto updateDto) {
        Long currentUserId = getCurrentUserId();

        // 1. 약을 찾고 권한 확인
        UserMedication medication = repo.findById(medicationId)
                .orElseThrow(() -> new RuntimeException("ID에 해당하는 약을 찾을 수 없습니다: " + medicationId));

        if (!medication.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        // 2. DTO의 내용으로 약 정보 덮어쓰기
        medication.setName(updateDto.getName());
        medication.setCategory(updateDto.getCategory());
        medication.setMemo(updateDto.getMemo());
        medication.setDoseUnitQuantity(updateDto.getDoseUnitQuantity());
        medication.setCurrentQuantity(updateDto.getCurrentQuantity());
        medication.setRefillThreshold(updateDto.getRefillThreshold());

        return repo.save(medication);
    }

    // 3. 기존 약에 새로운 스케줄을 추가하는 메서드
    @Transactional
    public IntakeSchedule addSchedule(Long medicationId, IntakeScheduleDto dto) {
        Long currentUserId = getCurrentUserId();
        UserMedication medication = repo.findById(medicationId)
                .orElseThrow(() -> new RuntimeException("ID에 해당하는 약을 찾을 수 없습니다: " + medicationId));

        if (!medication.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        IntakeSchedule newSchedule = new IntakeSchedule();
        newSchedule.setMedication(medication);
        newSchedule.setUser(medication.getUser());
        newSchedule.setIntakeTime(dto.getIntakeTime());
        newSchedule.setFrequency(dto.getFrequency());
        newSchedule.setStartDate(dto.getStartDate());
        newSchedule.setEndDate(dto.getEndDate());

        medication.getSchedules().add(newSchedule);
        repo.save(medication);
        return newSchedule;
    }

    // 4. 전체 조회(findAllWithLogs) 메서드
    public List<MedicationWithLogsDto> findAllWithLogs() {
        Long currentUserId = getCurrentUserId();
        List<UserMedication> medications = repo.findByUserId(currentUserId);

        if (medications.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> allScheduleIds = medications.stream()
                .flatMap(med -> med.getSchedules().stream())
                .map(IntakeSchedule::getId)
                .collect(Collectors.toList());

        Map<Long, MedicationIntakeLog> logMap = new HashMap<>();
        if (!allScheduleIds.isEmpty()) {
            LocalDate today = LocalDate.now();
            List<MedicationIntakeLog> todayLogs = logRepo.findLogsByScheduleIdsAndDate(
                    allScheduleIds,
                    today.atStartOfDay(),
                    today.plusDays(1).atStartOfDay()
            );
            logMap = todayLogs.stream()
                    .collect(Collectors.toMap(MedicationIntakeLog::getScheduleId, log -> log));
        }

        Map<Long, MedicationIntakeLog> finalLogMap = logMap;
        return medications.stream().map(med -> {
            MedicationWithLogsDto medDto = new MedicationWithLogsDto();
            medDto.setMedicationId(med.getId());
            medDto.setName(med.getName());
            medDto.setCategory(med.getCategory());

            // (isPublic 기능은 사용하지 않으므로 DTO에서 제거함. service.java에서도 제거)
            // medDto.setIsPublic(med.getIsPublic());

            // "다음 복용 시간" 계산
            String nextIntake = calculateNextIntakeTime(med.getSchedules());
            medDto.setNextIntakeTime(nextIntake);

            // 스케줄별 로그 상태 매핑
            List<ScheduleLogStatusDto> scheduleDtos = med.getSchedules().stream().map(sc -> {
                ScheduleLogStatusDto scDto = new ScheduleLogStatusDto();
                scDto.setScheduleId(sc.getId());
                scDto.setIntakeTime(sc.getIntakeTime());
                scDto.setFrequency(sc.getFrequency());
                scDto.setStartDate(sc.getStartDate());
                scDto.setEndDate(sc.getEndDate());

                if (finalLogMap.containsKey(sc.getId())) {
                    MedicationIntakeLog log = finalLogMap.get(sc.getId());
                    scDto.setLogId(log.getId());
                    scDto.setIntakeStatus(log.getIntakeStatus());
                    if (log.getRecordTime() != null) {
                        scDto.setRecordTime(log.getRecordTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    }
                }
                return scDto;
            }).collect(Collectors.toList());

            medDto.setSchedulesWithLogs(scheduleDtos);
            return medDto;
        }).collect(Collectors.toList());
    }

    // 5. ID로 찾기(findById) 메서드
    public UserMedication findById(Long id) {
        Long currentUserId = getCurrentUserId();
        UserMedication medication = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 ID의 약을 찾을 수 없습니다: " + id));

        if (!medication.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("해당 약에 대한 권한이 없습니다.");
        }
        return medication;
    }

    // 6. 삭제(delete) 메서드
    @Transactional
    public void delete(Long id) {
        UserMedication medication = findById(id);
        repo.deleteById(medication.getId());
    }

    // --- (헬퍼 메서드) ---

    // 다음 복용 시간을 계산하는 헬퍼 메서드
    private String calculateNextIntakeTime(List<IntakeSchedule> schedules) {
        if (schedules == null || schedules.isEmpty()) {
            return "스케줄 없음";
        }

        LocalDateTime now = LocalDateTime.now();
        List<LocalDateTime> candidates = new ArrayList<>();

        for (int i = 0; i < 7; i++) { // 향후 7일간 탐색
            LocalDate checkDate = now.toLocalDate().plusDays(i);
            for (IntakeSchedule schedule : schedules) {
                if (isScheduleActiveOn(schedule, checkDate)) {
                    LocalDateTime scheduleDateTime = checkDate.atTime(schedule.getIntakeTime());
                    if (scheduleDateTime.isAfter(now)) {
                        candidates.add(scheduleDateTime);
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            return "예정된 복용 없음";
        }

        LocalDateTime earliest = Collections.min(candidates);

        LocalDate today = now.toLocalDate();
        LocalDate tomorrow = today.plusDays(1);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        if (earliest.toLocalDate().isEqual(today)) {
            return "다음: 오늘 " + earliest.format(timeFormatter);
        } else if (earliest.toLocalDate().isEqual(tomorrow)) {
            return "다음: 내일 " + earliest.format(timeFormatter);
        } else {
            return "다음: " + earliest.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
        }
    }

    // 특정 날짜에 스케줄이 유효한지 확인하는 헬퍼 메서드
    private boolean isScheduleActiveOn(IntakeSchedule schedule, LocalDate date) {
        boolean inDateRange = !date.isBefore(schedule.getStartDate()) &&
                (schedule.getEndDate() == null || !date.isAfter(schedule.getEndDate()));
        if (!inDateRange) return false;

        String frequency = schedule.getFrequency();
        if (frequency == null || frequency.trim().isEmpty()) return false;

        if (frequency.equalsIgnoreCase("DAILY") || frequency.equals("매일")) {
            return true;
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        String dayKorean = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN);

        return frequency.contains(dayKorean) || frequency.contains(dayOfWeek.name().substring(0, 3));
    }
}
