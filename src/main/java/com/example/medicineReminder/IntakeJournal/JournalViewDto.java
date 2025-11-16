package com.example.medicineReminder.IntakeJournal;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class JournalViewDto {
    private Long journalId;
    private LocalDateTime journalTime;
    private String conditionEmoji;
    private String logMemo;
    // (이 일지에 묶인 상세 기록 목록)
    private List<JournalLogDetailDto> logs;
}