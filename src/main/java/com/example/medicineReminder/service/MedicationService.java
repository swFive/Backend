package com.example.medicineReminder.service;

import com.example.medicineReminder.domain.entity.UserMedications;
import com.example.medicineReminder.domain.repository.UserMedicationsRepository;
import com.example.medicineReminder.web.dto.medication.MedicationCreateRequest;
import com.example.medicineReminder.web.dto.medication.MedicationPatchRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MedicationService
{
    private final UserMedicationsRepository repo;

    public MedicationService(UserMedicationsRepository repo)
    {
        this.repo = repo;
    }

    @Transactional
    public Long create(MedicationCreateRequest req)
    {
        if (req == null) {
            throw new IllegalArgumentException("request is null");
        }
        if (req.userId() == null) {
            throw new IllegalArgumentException("userId is required");
        }
        if (req.name() == null || req.name().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (req.doseUnitQuantity() == null || req.doseUnitQuantity() <= 0) {
            throw new IllegalArgumentException("doseUnitQuantity must be > 0");
        }
        validateNonNegative("initialQuantity", req.initialQuantity());
        validateNonNegative("currentQuantity", req.currentQuantity());
        validateNonNegative("refillThreshold", req.refillThreshold());

        // currentQuantity가 비어 있으면 initialQuantity로 기본 설정
        Integer currentQuantity = (req.currentQuantity() != null)
                ? req.currentQuantity()
                : req.initialQuantity();

        UserMedications m = new UserMedications();
        m.setUserId(req.userId());
        m.setName(req.name());
        m.setCategory(req.category());
        m.setIsPublic(req.isPublic());
        m.setInitialQuantity(req.initialQuantity());
        m.setCurrentQuantity(currentQuantity);
        m.setDoseUnitQuantity(req.doseUnitQuantity());
        m.setRefillThreshold(req.refillThreshold());

        return repo.save(m).getMedicationId();
    }

    @Transactional
    public void patch(Long medicationId, MedicationPatchRequest req)
    {
        if (req == null) {
            throw new IllegalArgumentException("no fields to update");
        }
        boolean hasAny =
                req.currentQuantity() != null ||
                        req.doseUnitQuantity() != null ||
                        req.refillThreshold() != null;
        if (!hasAny) {
            throw new IllegalArgumentException("no fields to update");
        }

        UserMedications m = repo.findById(medicationId)
                .orElseThrow(() -> new NoSuchElementException("medication not found"));

        validateNonNegative("currentQuantity", req.currentQuantity());
        validateNonNegative("doseUnitQuantity", req.doseUnitQuantity());
        validateNonNegative("refillThreshold", req.refillThreshold());

        if (req.currentQuantity() != null) {
            m.setCurrentQuantity(req.currentQuantity());
        }
        if (req.doseUnitQuantity() != null) {
            if (req.doseUnitQuantity() == 0) {
                throw new IllegalArgumentException("doseUnitQuantity must be > 0");
            }
            m.setDoseUnitQuantity(req.doseUnitQuantity());
        }
        if (req.refillThreshold() != null) {
            m.setRefillThreshold(req.refillThreshold());
        }

        repo.save(m);
    }

    @Transactional(readOnly = true)
    public List<UserMedications> listByUser(Long userId)
    {
        return repo.findByUserIdOrderByMedicationIdAsc(userId);
    }

    private void validateNonNegative(String field, Integer value)
    {
        if (value != null && value < 0) {
            throw new IllegalArgumentException(field + " must be >= 0");
        }
    }
}
