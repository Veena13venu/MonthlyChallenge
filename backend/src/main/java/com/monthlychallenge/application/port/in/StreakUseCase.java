package com.monthlychallenge.application.port.in;

import com.monthlychallenge.domain.model.Streak;

import java.util.UUID;

/**
 * Inbound port — streak queries (FR-19, FR-20).
 */
public interface StreakUseCase {

    /** Returns the current streak record for the user. */
    Streak getStreak(UUID userId);
}
