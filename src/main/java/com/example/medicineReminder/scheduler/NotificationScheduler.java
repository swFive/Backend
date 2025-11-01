package com.example.medicineReminder.scheduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;


import com.example.medicineReminder.domain.entity.IntakeSchedules;
import com.example.medicineReminder.domain.repository.IntakeSchedulesRepository;
import com.example.medicineReminder.domain.repository.MedicationIntakeLogsRepository;
import com.example.medicineReminder.domain.repository.NotificationSendLogsRepository;
import com.example.medicineReminder.domain.repository.UserNotificationSettingsRepository;
import com.example.medicineReminder.service.NotificationService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;
import java.util.Map;

// 스케줄러 비활성
@ConditionalOnProperty(name = "app.scheduler.enabled", havingValue = "true", matchIfMissing = true)

@EnableScheduling
@Component
public class NotificationScheduler{

    private final IntakeSchedulesRepository scheduleRepo;
    private final MedicationIntakeLogsRepository logsRepo;
    private final NotificationSendLogsRepository sendLogsRepo;
    private final UserNotificationSettingsRepository settingRepo;
    private final NotificationService notificationService;

    public NotificationScheduler(IntakeSchedulesRepository scheduleRepo,
                                 MedicationIntakeLogsRepository logsRepo,
                                 NotificationSendLogsRepository sendLogsRepo,
                                 UserNotificationSettingsRepository settingRepo,
                                 NotificationService notificationService) {
        this.scheduleRepo = scheduleRepo;
        this.logsRepo = logsRepo;
        this.sendLogsRepo = sendLogsRepo;
        this.settingRepo = settingRepo;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void dispatch() {
        LocalTime now = LocalTime.now();
        LocalTime windowFrom = now.minusMinutes(1);
        LocalTime windowTo = now.plusMinutes(1);

        for (long userId = 1L; userId <= 100000L; userId++) {
            // 사용자 전체 알림 옵트아웃: UserNotificationSettings.isRepeat == false 라면 아무 것도 보내지 않음
            var setting = settingRepo.findByUserId(userId).orElse(null);
            if (setting != null && Boolean.FALSE.equals(setting.getIsRepeat())) {
                continue;
            }

            List<IntakeSchedules> list = scheduleRepo.findByUserIdAndIntakeTimeBetween(userId, windowFrom, windowTo);
            for (IntakeSchedules s : list) {
                if (!Boolean.TRUE.equals(s.getIsActive())) continue;

                // 예정 시각 계산(오늘 기준). 필요시 lead_minutes 반영 가능.
                LocalDateTime scheduled = LocalDateTime.of(LocalDate.now(), s.getIntakeTime());

                // 복용 완료(TAKEN/LATE)가 이미 기록되었으면 모든 알림(재알림 포함) 중단 —— 핵심 한 줄
                if (logsRepo.existsTakenOrLateSince(s.getUserId(), s.getScheduleId(), scheduled)) {
                    continue;
                }

                // 1차 알림
                notificationService.sendToUserForSchedule(
                        s.getUserId(), s.getScheduleId(),
                        "복용 알림", "설정된 복용 시간입니다.",
                        Map.of("route", "/intake")
                );

                // 재알림: 사용자가 켠 경우에만, 간격 지났는지 확인
                if (Boolean.TRUE.equals(s.getReNotifyEnabled())) {
                    var lastSent = sendLogsRepo.findLastSentTimeByUserAndSchedule(s.getUserId(), s.getScheduleId());
                    boolean needReNotify = false;
                    if (lastSent != null) {
                        long minutes = Duration.between(lastSent, LocalDateTime.now()).toMinutes();
                        needReNotify = minutes >= s.getReNotifyIntervalMin();
                    }
                    if (needReNotify &&
                            !logsRepo.existsTakenOrLateSince(s.getUserId(), s.getScheduleId(), scheduled)) {
                        notificationService.sendToUserForSchedule(
                                s.getUserId(), s.getScheduleId(),
                                "복용 재알림", "아직 복용 완료 확인이 없습니다.",
                                Map.of("route", "/intake", "type", "renotify")
                        );
                    }
                }
            }
        }
    }
}


