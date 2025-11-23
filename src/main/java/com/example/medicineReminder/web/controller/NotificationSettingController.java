package com.example.medicineReminder.web.controller;

import com.example.medicineReminder.service.NotificationSettingService;
import com.example.medicineReminder.web.dto.notify.NotificationSettingRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/notification-settings")
public class NotificationSettingController
{
    private final NotificationSettingService service;

    public NotificationSettingController(NotificationSettingService service)
    {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> upsert(@RequestBody @Validated NotificationSettingRequest req)
    {
        boolean created = service.upsert(req);
        if (created) {
            // userId 하나당 1행이므로 userId를 키처럼 사용
            return ResponseEntity.created(URI.create("/api/notification-settings/user/" + req.userId())).build();
        }
        return ResponseEntity.ok().build();
    }
}
