package com.example.medicineReminder.service;

import com.example.medicineReminder.domain.entity.UserNotificationSettings;
import com.example.medicineReminder.domain.repository.UserNotificationSettingsRepository;
import com.example.medicineReminder.web.dto.notify.NotificationSettingRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationSettingService
{
    private final UserNotificationSettingsRepository repo;

    public NotificationSettingService(UserNotificationSettingsRepository repo)
    {
        this.repo = repo;
    }

    @Transactional
    public boolean upsert(NotificationSettingRequest req)
    {
        // reNotifyInterval 규칙 정리: isRepeat=false면 0 강제
        Integer interval = (req.isRepeat() != null && !req.isRepeat())
                ? 0
                : (req.reNotifyInterval() != null ? req.reNotifyInterval() : 0);

        return repo.findByUserId(req.userId())
                .map(existing -> {
                    existing.setNotifyTimeOffset(req.notifyTimeOffset());
                    existing.setIsRepeat(req.isRepeat());
                    existing.setReNotifyInterval(interval);
                    repo.save(existing);
                    return false; // updated
                })
                .orElseGet(() -> {
                    UserNotificationSettings s = new UserNotificationSettings();
                    s.setUserId(req.userId());
                    s.setNotifyTimeOffset(req.notifyTimeOffset());
                    s.setIsRepeat(req.isRepeat());
                    s.setReNotifyInterval(interval);
                    repo.save(s);
                    return true; // created
                });
    }
}
