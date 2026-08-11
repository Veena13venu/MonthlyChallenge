package com.monthlychallenge.domain.models;

import com.monthlychallenge.domain.enums.CheckInStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CheckIn {
    private UUID id;
    private UUID userId;
    private UUID challengeId;
    private LocalDate date;
    private CheckInStatus status;
    private Double actualValue;
    private Instant createdAt;
    private Instant updatedAt;

    public double pointValue() {
        if (status == null) return 0.0;
        return switch (status) {
            case COMPLETED -> 1.0;
            case HALF_COMPLETED -> 0.5;
            case MISSED -> 0.0;
        };
    }
}
