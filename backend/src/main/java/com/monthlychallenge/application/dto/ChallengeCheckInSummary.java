package com.monthlychallenge.application.dto;

import java.util.UUID;

public record ChallengeCheckInSummary(
        UUID challengeId,
        long completedCount,
        long halfCompletedCount,
        long missedCount
) {}
