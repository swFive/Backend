package com.example.medicineReminder.web.controller;

import com.example.medicineReminder.service.FcmTokenService;
import com.example.medicineReminder.web.dto.notify.NotificationTokenRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/notifications/token")
public class NotificationTokenController
{
    private final FcmTokenService service;

    public NotificationTokenController(FcmTokenService service)
    {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> upsert(@RequestBody @Validated NotificationTokenRequest req)
    {
        boolean created = service.upsert(req.userId(), req.fcmToken());
        if (created) {
            return ResponseEntity.created(URI.create("/api/notifications/token/" + req.fcmToken())).build();
        }
        return ResponseEntity.ok().build();
    }
}
