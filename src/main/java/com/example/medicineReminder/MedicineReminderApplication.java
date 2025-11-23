package com.example.medicineReminder;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class MedicineReminderApplication {
    public static void main(String[] args) {

        // 1) .env 로드 (.env 없으면 무시)
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // 2) .env -> System env 순으로 값 선택
        String dbUrl  = pick(dotenv.get("DB_URL"),  System.getenv("DB_URL"),
                "jdbc:mysql://localhost:3306/reminder_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul");
        String dbUser = pick(dotenv.get("DB_USERNAME"), System.getenv("DB_USERNAME"), "root");
        String dbPass = pick(dotenv.get("DB_PASSWORD"), System.getenv("DB_PASSWORD"), "");
        String port   = pick(dotenv.get("SERVER_PORT"), System.getenv("SERVER_PORT"), "8080");

        // 이 4줄 추가 <- 플레이스홀더가 확실히 해소되도록 보장
        System.setProperty("DB_URL", dbUrl);
        System.setProperty("DB_USERNAME", dbUser);
        System.setProperty("DB_PASSWORD", dbPass);
        System.setProperty("SERVER_PORT", port);

        // 3) Spring 기본 프로퍼티로 주입
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("spring.datasource.url", dbUrl);
        defaults.put("spring.datasource.username", dbUser);
        defaults.put("spring.datasource.password", dbPass);
        defaults.put("server.port", port);

        // Dialect 명시(연쇄 오류 예방)
        defaults.put("spring.jpa.database-platform", "org.hibernate.dialect.MySQLDialect");

        SpringApplication app = new SpringApplication(MedicineReminderApplication.class);
        app.setDefaultProperties(defaults);
        app.run(args);
    }

    private static String pick(String a, String b, String fallback) {
        if (a != null && !a.isBlank()) return a;
        if (b != null && !b.isBlank()) return b;
        return fallback;
    }
}

/* DB 카멜 쓰는 중!!

insert
    into
        MedicationIntakeLogs
        (intake_status, late_minutes, record_time, schedule_id, user_id)
    values
        (?, ?, ?, ?, ?)
 */