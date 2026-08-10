package com.monthlychallenge.application.port.in;

import com.monthlychallenge.application.port.in.command.CreateChallengeCommand;
import com.monthlychallenge.application.port.in.command.UpdateChallengeCommand;
import com.monthlychallenge.domain.model.Challenge;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

/**
 * Inbound port — challenge management use cases (FR-05 to FR-10).
 */
public interface ChallengeUseCase {

    /** Creates a new challenge for the authenticated user (FR-05, FR-06, FR-07). */
    Challenge createChallenge(UUID userId, CreateChallengeCommand command);

    /** Updates an existing challenge (FR-08). */
    Challenge updateChallenge(UUID userId, UUID challengeId, UpdateChallengeCommand command);

    /** Soft-deletes (archives) a challenge (FR-08). */
    void deleteChallenge(UUID userId, UUID challengeId);

    /** Returns all active challenges for the user in the given month. */
    List<Challenge> getChallengesForMonth(UUID userId, YearMonth month);

    /** Returns challenges due today for the home-screen check-in list. */
    List<Challenge> getTodaysChallenges(UUID userId);

    /** Carries forward challenges from a prior month into the new month (FR-09). */
    List<Challenge> rolloverChallenges(UUID userId, YearMonth fromMonth, YearMonth toMonth);

    /** Returns the pre-defined challenge template library (FR-10). */
    List<com.monthlychallenge.domain.model.ChallengeTemplate> getChallengeTemplates();
}
