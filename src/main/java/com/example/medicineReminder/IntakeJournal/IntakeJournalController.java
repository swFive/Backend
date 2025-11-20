package com.example.medicineReminder.IntakeJournal;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journals")
public class IntakeJournalController {

    private final IntakeJournalService journalService;

    public IntakeJournalController(IntakeJournalService journalService) {
        this.journalService = journalService;
    }

    // (기존 GET: 목록 조회)
    @GetMapping
    public List<JournalViewDto> getJournalsForUser() {
        return journalService.getJournalsForUser();
    }

    // === [신규] 일지 수정 API ===
    @PutMapping("/{journalId}")
    public ResponseEntity<IntakeJournal> updateJournal(
            @PathVariable Long journalId,
            @RequestBody JournalCreateDto updateDto) { // DTO 재사용 (이모지, 메모 등)
        IntakeJournal updatedJournal = journalService.updateJournal(journalId, updateDto);
        return new ResponseEntity<>(updatedJournal, HttpStatus.OK);
    }

    // === [신규] 일지 삭제 API ===
    @DeleteMapping("/{journalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJournal(@PathVariable Long journalId) {
        journalService.deleteJournal(journalId);
    }
}