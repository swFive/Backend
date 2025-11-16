package com.example.medicineReminder.mediinfo;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class controller {
    private final service service;

    public controller(service service) {
        this.service = service;
    }

    @PostMapping
    public UserMedication create(@Valid @RequestBody dto dto) {
        return service.save(dto);
    }

    @GetMapping
    public List<MedicationWithLogsDto> getAll() {
        return service.findAllWithLogs();
    }

    @GetMapping("/{id}")
    public UserMedication getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    // === [핵심 수정] 약 기본 정보 업데이트 API 추가 ===
    @PutMapping("/{id}")
    public UserMedication updateMedication(
            @PathVariable Long id,
            @Valid @RequestBody MedicationUpdateDto updateDto) {
        return service.updateMedication(id, updateDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // (기능을 사용하지 않기로 했으므로 주석 처리 또는 삭제)
    /*
    @PatchMapping("/{id}/toggle-public")
    public UserMedication togglePublic(@PathVariable Long id) {
        return service.togglePublicStatus(id);
    }
    */

    @PostMapping("/{medicationId}/schedules")
    public IntakeSchedule addSchedule(
            @PathVariable Long medicationId,
            @Valid @RequestBody IntakeScheduleDto dto) {
        return service.addSchedule(medicationId, dto);
    }
}