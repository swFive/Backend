package com.example.medicineReminder.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.example.medicineReminder.domain.entity.FcmDeviceTokens;
import com.example.medicineReminder.domain.entity.NotificationSendLogs;
import com.example.medicineReminder.domain.repository.FcmDeviceTokensRepository;
import com.example.medicineReminder.domain.repository.NotificationSendLogsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final FcmDeviceTokensRepository tokenRepo;
    private final NotificationSendLogsRepository logRepo;

    @Value("${reminder.fcm.enabled:true}")
    private boolean fcmEnabled;

    public NotificationService(FcmDeviceTokensRepository tokenRepo,
                                      NotificationSendLogsRepository logRepo) {
        this.tokenRepo = tokenRepo;
        this.logRepo = logRepo;
    }

    @Transactional
    public void sendToUser(Long userId, String title, String body, Map<String, String> data) {
        List<FcmDeviceTokens> tokens = tokenRepo.findByUserIdAndIsActiveTrue(userId);
        if (tokens.isEmpty()) return;

        final ObjectMapper mapper = new ObjectMapper();

        for (FcmDeviceTokens t : tokens) {
            NotificationSendLogs log = new NotificationSendLogs();
            try {
                var c = NotificationSendLogs.class;
                c.getDeclaredField("userId").setAccessible(true);
                c.getDeclaredField("tokenId").setAccessible(true);
                c.getDeclaredField("title").setAccessible(true);
                c.getDeclaredField("body").setAccessible(true);
                c.getDeclaredField("dataJson").setAccessible(true);
                c.getDeclaredField("status").setAccessible(true);
                c.getDeclaredField("errorCode").setAccessible(true);
                c.getDeclaredField("errorMessage").setAccessible(true);
                c.getDeclaredField("createdAt").setAccessible(true);

                c.getDeclaredField("userId").set(log, userId);
                c.getDeclaredField("tokenId").set(log, t.getTokenId());
                c.getDeclaredField("title").set(log, title);
                c.getDeclaredField("body").set(log, body);
                String json = (data == null || data.isEmpty()) ? null : mapper.writeValueAsString(data);
                c.getDeclaredField("dataJson").set(log, json);
                c.getDeclaredField("createdAt").set(log, java.time.LocalDateTime.now());

                if (!fcmEnabled) {
                    c.getDeclaredField("status").set(log, NotificationSendLogs.Status.FAILURE);
                    c.getDeclaredField("errorCode").set(log, "DISABLED");
                    c.getDeclaredField("errorMessage").set(log, "FCM disabled");
                    logRepo.save(log);
                    continue;
                }

                var mb = com.google.firebase.messaging.Message.builder()
                        .setToken(t.getFcmToken())
                        .putAllData(data == null ? java.util.Map.of() : data);

                String messageId = com.google.firebase.messaging.FirebaseMessaging.getInstance().send(mb.build());
                c.getDeclaredField("status").set(log, NotificationSendLogs.Status.SUCCESS);
                c.getDeclaredField("errorCode").set(log, null);
                c.getDeclaredField("errorMessage").set(log, messageId);

            } catch (Exception e) {
                try {
                    var c = NotificationSendLogs.class;
                    c.getDeclaredField("status").setAccessible(true);
                    c.getDeclaredField("errorCode").setAccessible(true);
                    c.getDeclaredField("errorMessage").setAccessible(true);
                    c.getDeclaredField("status").set(log, NotificationSendLogs.Status.FAILURE);
                    c.getDeclaredField("errorCode").set(log, "EXCEPTION");
                    c.getDeclaredField("errorMessage").set(log, e.getClass().getSimpleName());
                } catch (Exception ignore) {}
            }
            logRepo.save(log);
        }
    }

    public void sendToUserForSchedule(Long userId,
                                      Long scheduleId,
                                      String title,       // 기존 매개변수명: 복용_재알림
                                      String body,        // 기존 매개변수명: s
                                      Map<String, String> data) {
        Map<String, String> payload = (data == null)
                ? new java.util.HashMap<>()
                : new java.util.HashMap<>(data);
        // 스케줄별 재알림 판정용으로 로그 data_json에 scheduleId를 함께 기록
        payload.put("scheduleId", String.valueOf(scheduleId));

        // 공용 발송 로직 재사용
        sendToUser(userId, title, body, payload);
    }
}

