package com.monthlychallenge.application.ports.out;

import com.monthlychallenge.application.dto.ChallengeCheckInSummary;
import com.monthlychallenge.domain.models.CheckIn;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CheckInRepository {
    CheckIn save(CheckIn checkIn);
    Optional<CheckIn> findByUserIdAndChallengeIdAndDate(UUID userId, UUID challengeId, LocalDate date);
    List<CheckIn> findByUserIdAndDate(UUID userId, LocalDate date);
    List<CheckIn> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to);
    List<ChallengeCheckInSummary> summariseByChallenge(UUID userId, LocalDate from, LocalDate to);
}
