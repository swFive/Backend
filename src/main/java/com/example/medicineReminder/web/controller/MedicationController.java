package com.example.medicineReminder.web.controller;

import com.example.medicineReminder.domain.entity.UserMedications;
import com.example.medicineReminder.service.MedicationService;
import com.example.medicineReminder.web.dto.medication.MedicationCreateRequest;
import com.example.medicineReminder.web.dto.medication.MedicationPatchRequest;
import com.example.medicineReminder.web.dto.medication.MedicationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/medications")
public class MedicationController
{
    private final MedicationService service;

    public MedicationController(MedicationService service)
    {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Validated MedicationCreateRequest req)
    {
        Long id = service.create(req);
        return ResponseEntity.created(URI.create("/api/medications/" + id)).body(id);
    }

    @PatchMapping("/{medicationId}")
    public ResponseEntity<Void> patch(@PathVariable Long medicationId,
                                      @RequestBody MedicationPatchRequest req)
    {
        service.patch(medicationId, req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MedicationResponse>> listByUser(@PathVariable Long userId)
    {
        List<UserMedications> list = service.listByUser(userId);
        return ResponseEntity.ok(list.stream().map(MedicationResponse::from).toList());
    }
}
