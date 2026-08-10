package com.monthlychallenge.application.port.out;

import com.monthlychallenge.domain.model.Streak;

import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port — persistence contract for {@link Streak}.
 */
public interface StreakRepository {

    Streak save(Streak streak);

    Optional<Streak> findByUserId(UUID userId);
}
