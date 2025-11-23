package com.example.medicineReminder.service;

import com.example.medicineReminder.domain.entity.IntakeSchedules;
import com.example.medicineReminder.domain.repository.IntakeSchedulesRepository;
import com.example.medicineReminder.web.dto.schedule.ScheduleCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ScheduleService
{
    private final IntakeSchedulesRepository repo;

    public ScheduleService(IntakeSchedulesRepository repo)
    {
        this.repo = repo;
    }

    @Transactional
    public Long create(ScheduleCreateRequest req)
    {
        IntakeSchedules s = new IntakeSchedules();

        s.setMedicationId(req.medicationId());
        s.setUserId(req.userId());
        s.setIntakeTime(LocalTime.parse(req.intakeTime()));
        s.setFrequency(req.frequency());
        s.setStartDate(LocalDate.parse(req.startDate()));

        if (req.endDate() != null && !req.endDate().isBlank())
        {
            s.setEndDate(LocalDate.parse(req.endDate()));
        }

        // null 허용 필드 기본값 처리
        s.setLeadMinutes(req.leadMinutes() == null ? 0 : req.leadMinutes());
        s.setIsRepeat(req.isRepeat() == null ? true : req.isRepeat());
        s.setReNotifyEnabled(req.reNotifyEnabled() == null ? true : req.reNotifyEnabled());
        s.setReNotifyIntervalMin(req.reNotifyIntervalMin() == null ? 5 : req.reNotifyIntervalMin());
        s.setIsActive(true);

        return repo.save(s).getScheduleId();
    }

    @Transactional
    public void updateReNotify(Long scheduleId, Boolean enabled, Integer intervalMin)
    {
        IntakeSchedules s = repo.findById(scheduleId).orElseThrow();

        if (enabled != null)
        {
            s.setReNotifyEnabled(enabled);
        }
        if (intervalMin != null && intervalMin >= 1)
        {
            s.setReNotifyIntervalMin(intervalMin);
        }
    }

    @Transactional(readOnly = true)
    public List<IntakeSchedules> listByUser(Long userId)
    {
        return repo.findByUserIdAndIsActiveTrueOrderByIntakeTimeAsc(userId);
    }
}
