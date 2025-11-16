package com.example.medicineReminder.IntakeJournal;

import org.springframework.data.jpa.repository.EntityGraph; // [추가]
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // [추가]

public interface IntakeJournalRepository extends JpaRepository<IntakeJournal, Long> {

    // === [핵심 수정] '일지 목록' 조회를 위한 쿼리 추가 ===
    // (N+1 문제 방지를 위해 'logs'를 함께 fetch join)
    @EntityGraph(attributePaths = {"logs"})
    List<IntakeJournal> findByUserIdOrderByJournalTimeDesc(Long userId);
}