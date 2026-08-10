package com.monthlychallenge.application.port.out;

import com.monthlychallenge.domain.model.Challenge;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port — persistence contract for {@link Challenge}.
 */
public interface ChallengeRepository {

    Challenge save(Challenge challenge);

    Optional<Challenge> findByIdAndOwnerId(UUID id, UUID ownerId);

    List<Challenge> findActiveByOwnerIdAndMonth(UUID ownerId, YearMonth month);

    /** Returns all users who have a reminder set at the given hour:minute (for notifications). */
    List<Challenge> findAllWithReminderAt(int hour, int minute);
}
