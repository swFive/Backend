package com.example.medicineReminder.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "UserMedications",
        uniqueConstraints = @UniqueConstraint(name = "uk_medication_user", columnNames = {"medication_id", "user_id"}))
public class UserMedications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medication_id")
    private Long medicationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "initial_quantity")
    private Integer initialQuantity;

    @Column(name = "current_quantity")
    private Integer currentQuantity;

    @Column(name = "dose_unit_quantity")
    private Integer doseUnitQuantity;

    @Column(name = "refill_threshold")
    private Integer refillThreshold;

    @Column(name = "last_recalculated_at")
    private LocalDateTime lastRecalculatedAt;
}
