package com.example.medicineReminder.IntakeJournal;

import com.example.medicineReminder.domain.entity.AppUsers;
import com.example.medicineReminder.medication_log.MedicationIntakeLog;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "IntakeJournals")
public class IntakeJournal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "journal_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUsers user; // ('mediinfo' 패키지의 AppUser)

    @Column(name = "journal_time", nullable = false)
    private LocalDateTime journalTime;

    @Column(name = "condition_emoji", length = 10)
    private String conditionEmoji;

    @Column(name = "log_memo", columnDefinition = "TEXT")
    private String logMemo;

    @JsonManagedReference
    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL)
    private List<MedicationIntakeLog> logs;
}