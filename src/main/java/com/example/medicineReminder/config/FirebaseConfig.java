package com.example.medicineReminder.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class FirebaseConfig {

    @Value("${reminder.fcm.enabled:true}")
    private boolean enabled;

    @Value("${reminder.fcm.useGoogleCredentialsPath:true}")
    private boolean useGooglePath;

    @Value("${reminder.fcm.jsonEnvKey:FIREBASE_CREDENTIALS_JSON}")
    private String jsonEnvKey;

    @PostConstruct
    public void init() {
        if (!enabled) return;
        try {
            if (FirebaseApp.getApps() != null && !FirebaseApp.getApps().isEmpty()) return;

            FirebaseOptions options;
            if (useGooglePath) {
                // GOOGLE_APPLICATION_CREDENTIALS 환경변수 경로 사용
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.getApplicationDefault())
                        .build();
            } else {
                // 서비스계정 JSON을 환경변수에 그대로 담아둔 경우
                String json = System.getenv(jsonEnvKey);
                if (json == null || json.isBlank()) throw new IllegalStateException("FCM JSON env not found: " + jsonEnvKey);
                ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(bais))
                        .build();
            }
            FirebaseApp.initializeApp(options);
        } catch (Exception ignore) {
            // 수업 범위: 초기화 실패 시에도 앱은 구동. 실제 발송은 실패로 기록됨.
        }
    }
}

