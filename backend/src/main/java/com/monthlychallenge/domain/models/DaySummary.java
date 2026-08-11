package com.monthlychallenge.domain.models;

import com.monthlychallenge.domain.enums.DayResult;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DaySummary {
    private UUID id;
    private UUID userId;
    private LocalDate date;
    private double totalPoints;
    private double minimumThreshold;
    private DayResult result;
    private Instant createdAt;

    public static DayResult evaluate(double pointsScored, double threshold) {
        if (threshold <= 0.0) return DayResult.SUCCESS;
        if (pointsScored >= threshold) return DayResult.SUCCESS;
        if (pointsScored > 0.0) return DayResult.PARTIAL;
        return DayResult.MISSED;
    }
}
