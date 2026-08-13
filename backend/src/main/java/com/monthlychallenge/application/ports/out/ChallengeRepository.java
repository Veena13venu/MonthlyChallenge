package com.monthlychallenge.application.ports.out;

import com.monthlychallenge.domain.models.Challenge;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChallengeRepository {
    Challenge save(Challenge challenge);
    Optional<Challenge> findByIdAndOwnerId(UUID id, UUID ownerId);
    List<Challenge> findActiveByOwnerIdAndMonth(UUID ownerId, YearMonth month);
    List<Challenge> findAllWithReminderAt(int hour, int minute);
}
