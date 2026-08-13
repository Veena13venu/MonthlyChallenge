package com.monthlychallenge.application.ports.in;

import com.monthlychallenge.application.ports.in.command.CreateChallengeCommand;
import com.monthlychallenge.application.ports.in.command.UpdateChallengeCommand;
import com.monthlychallenge.domain.models.Challenge;
import com.monthlychallenge.domain.models.ChallengeTemplate;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public interface ChallengeUseCase {
    Challenge createChallenge(UUID userId, CreateChallengeCommand command);
    Challenge updateChallenge(UUID userId, UUID challengeId, UpdateChallengeCommand command);
    void deleteChallenge(UUID userId, UUID challengeId);
    List<Challenge> getChallengesForMonth(UUID userId, YearMonth month);
    List<Challenge> getTodaysChallenges(UUID userId);
    List<Challenge> rolloverChallenges(UUID userId, YearMonth fromMonth, YearMonth toMonth);
    List<ChallengeTemplate> getChallengeTemplates();
}
