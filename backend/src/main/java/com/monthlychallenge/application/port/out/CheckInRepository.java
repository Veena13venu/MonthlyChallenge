package com.monthlychallenge.application.port.out;

import com.monthlychallenge.domain.model.CheckIn;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port — persistence contract for {@link CheckIn}.
 */
public interface CheckInRepository {

    CheckIn save(CheckIn checkIn);

    Optional<CheckIn> findByUserIdAndChallengeIdAndDate(UUID userId, UUID challengeId, LocalDate date);

    List<CheckIn> findByUserIdAndDate(UUID userId, LocalDate date);

    List<CheckIn> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to);

    /** Completion counts per challenge for the given month — used for dashboard rates. */
    List<com.monthlychallenge.application.dto.ChallengeCheckInSummary> summariseByChallenge(UUID userId, LocalDate from, LocalDate to);
}
