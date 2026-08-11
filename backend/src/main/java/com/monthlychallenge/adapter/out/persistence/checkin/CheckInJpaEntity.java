package com.monthlychallenge.adapter.out.persistence.checkin;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "check_ins")
@Getter @Setter @NoArgsConstructor
public class CheckInJpaEntity {

    @Id @Column(columnDefinition = "uuid") private UUID id;
    @Column(name = "user_id", nullable = false, columnDefinition = "uuid") private UUID userId;
    @Column(name = "challenge_id", nullable = false, columnDefinition = "uuid") private UUID challengeId;
    @Column(nullable = false) private LocalDate date;
    @Column(nullable = false, length = 20) private String status;
    @Column(name = "actual_value") private Double actualValue;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;

    public double getPointValue() {
        if (status == null) return 0.0;
        return switch (status) {
            case "COMPLETED" -> 1.0;
            case "HALF_COMPLETED" -> 0.5;
            default -> 0.0;
        };
    }
}
