package com.example.medicineReminder.IntakeJournal;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // [추가]

@RestController
@RequestMapping("/api/journals")
public class IntakeJournalController {

    private final IntakeJournalService journalService;

    public IntakeJournalController(IntakeJournalService journalService) {
        this.journalService = journalService;
    }

    /**
     * API: 복용 '일지'(그룹) 생성
     */
    @PostMapping
    public ResponseEntity<IntakeJournal> createJournal(
            @Valid @RequestBody JournalCreateDto journalDto) {

        IntakeJournal savedJournal = journalService.createJournal(journalDto);
        return new ResponseEntity<>(savedJournal, HttpStatus.CREATED);
    }


    /**
     * 현재 사용자의 모든 '복용 일지' 목록을 조회합니다.
     * @return '복용 일지' DTO 리스트 (시간 역순)
     */
    @GetMapping
    public List<JournalViewDto> getJournalsForUser() {
        return journalService.getJournalsForUser();
    }
}