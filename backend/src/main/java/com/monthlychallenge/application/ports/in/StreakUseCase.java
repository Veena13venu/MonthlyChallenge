package com.monthlychallenge.application.ports.in;

import com.monthlychallenge.domain.models.Streak;

import java.util.UUID;

public interface StreakUseCase {
    Streak getStreak(UUID userId);
}
