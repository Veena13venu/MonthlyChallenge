package com.monthlychallenge.application.dto;

import java.util.UUID;

public record ChallengeCompletionRate(
        UUID challengeId,
        String challengeTitle,
        int totalDueDays,
        int completedDays,
        int halfCompletedDays,
        int missedDays
) {
    public double completionPercentage() {
        if (totalDueDays == 0) return 0.0;
        double points = completedDays + halfCompletedDays * 0.5;
        return (points / totalDueDays) * 100.0;
    }
}
