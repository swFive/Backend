package com.example.medicineReminder.mediinfo;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// 빈 이름 명시: web.controller 쪽 MedicationController와 충돌 방지
@RestController("mediInfoMedicationController")
@RequestMapping("/api/mediinfo/medicines")  // mediinfo 전용 URL prefix
public class MedicationController {

    // mediinfo 패키지의 MedicationService
    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    // 약 등록
    @PostMapping
    public UserMedication create(@Valid @RequestBody MedicationDto medicationDto) {
        return medicationService.save(medicationDto);
    }

    // 약 + 오늘자 로그 전체 조회
    @GetMapping
    public List<MedicationWithLogsDto> getAll() {
        return medicationService.findAllWithLogs();
    }

    // 약 단건 조회
    @GetMapping("/{id}")
    public UserMedication getOne(@PathVariable Long id) {
        return medicationService.findById(id);
    }

    // 약 기본 정보 수정
    @PutMapping("/{id}")
    public UserMedication updateMedication(
            @PathVariable Long id,
            @Valid @RequestBody MedicationUpdateDto updateDto
    ) {
        return medicationService.updateMedication(id, updateDto);
    }

    // 약 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        medicationService.delete(id);
    }

    // (공개 여부 토글 기능은 사용하지 않으므로 주석 유지)
    /*
    @PatchMapping("/{id}/toggle-public")
    public UserMedication togglePublic(@PathVariable Long id) {
        return medicationService.togglePublicStatus(id);
    }
    */

    // 기존 약에 새로운 스케줄 추가
    @PostMapping("/{medicationId}/schedules")
    public IntakeSchedule addSchedule(
            @PathVariable Long medicationId,
            @Valid @RequestBody IntakeScheduleDto dto
    ) {
        return medicationService.addSchedule(medicationId, dto);
    }
}
