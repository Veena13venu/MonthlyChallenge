package com.monthlychallenge.application.ports.out;

import com.monthlychallenge.domain.models.Streak;

import java.util.Optional;
import java.util.UUID;

public interface StreakRepository {
    Streak save(Streak streak);
    Optional<Streak> findByUserId(UUID userId);
}
