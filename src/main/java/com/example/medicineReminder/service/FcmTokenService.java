package com.example.medicineReminder.service;

import com.example.medicineReminder.domain.entity.FcmDeviceTokens;
import com.example.medicineReminder.domain.repository.FcmDeviceTokensRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FcmTokenService
{
    private final FcmDeviceTokensRepository repo;

    public FcmTokenService(FcmDeviceTokensRepository repo)
    {
        this.repo = repo;
    }

    @Transactional
    public boolean upsert(Long userId, String fcmToken)
    {
        if (userId == null) {
            throw new IllegalArgumentException("userId required");
        }
        if (fcmToken == null || fcmToken.isBlank()) {
            throw new IllegalArgumentException("fcmToken required");
        }

        Optional<FcmDeviceTokens> existingByToken = repo.findByFcmToken(fcmToken);
        if (existingByToken.isPresent()) {
            FcmDeviceTokens t = existingByToken.get();
            if (!t.getUserId().equals(userId)) {
                // 다른 사용자에 묶여 있으면 요청 거절
                throw new IllegalArgumentException("token belongs to another user");
            }
            if (Boolean.FALSE.equals(t.getIsActive())) {
                t.setIsActive(true);
                repo.save(t);
            }
            return false; // updated(재활성화 또는 변화 없음)
        }

        // 새 토큰 등록
        FcmDeviceTokens t = new FcmDeviceTokens();
        t.setUserId(userId);
        t.setFcmToken(fcmToken);
        t.setIsActive(true);
        repo.save(t);
        return true; // created
    }
}
