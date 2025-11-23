// src/main/java/com/example/medicineReminder/domain/repository/FcmDeviceTokensRepository.java
package com.example.medicineReminder.domain.repository;

import com.example.medicineReminder.domain.entity.FcmDeviceTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmDeviceTokensRepository extends JpaRepository<FcmDeviceTokens, Long>
{
    List<FcmDeviceTokens> findByUserIdAndIsActiveTrue(Long userId);

    Optional<FcmDeviceTokens> findByFcmToken(String fcmToken);
}
