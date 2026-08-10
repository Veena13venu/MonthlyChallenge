package com.monthlychallenge.application.service;

import com.monthlychallenge.application.port.in.ChallengeUseCase;
import com.monthlychallenge.application.port.in.command.CreateChallengeCommand;
import com.monthlychallenge.application.port.in.command.UpdateChallengeCommand;
import com.monthlychallenge.application.port.out.ChallengeRepository;
import com.monthlychallenge.domain.model.*;
import com.monthlychallenge.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ChallengeService implements ChallengeUseCase {

    private final ChallengeRepository challengeRepository;

    public ChallengeService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Override
    public Challenge createChallenge(UUID userId, CreateChallengeCommand cmd) {
        Challenge challenge = Challenge.builder()
                .id(UUID.randomUUID()).ownerId(userId).title(cmd.title())
                .description(cmd.description()).category(cmd.category())
                .frequency(cmd.frequency()).target(cmd.target()).month(cmd.month())
                .visibility(cmd.visibility() != null ? cmd.visibility() : ChallengeVisibility.SHARED)
                .reminderHour(cmd.reminderHour()).reminderMinute(cmd.reminderMinute())
                .weeklyDueDays(cmd.weeklyDueDays()).monthlyDueDay(cmd.monthlyDueDay())
                .active(true).createdAt(Instant.now()).updatedAt(Instant.now())
                .build();
        return challengeRepository.save(challenge);
    }

    @Override
    public Challenge updateChallenge(UUID userId, UUID challengeId, UpdateChallengeCommand cmd) {
        Challenge existing = challengeRepository.findByIdAndOwnerId(challengeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
        return challengeRepository.save(existing
                .withTitle(cmd.title() != null ? cmd.title() : existing.getTitle())
                .withDescription(cmd.description() != null ? cmd.description() : existing.getDescription())
                .withCategory(cmd.category() != null ? cmd.category() : existing.getCategory())
                .withTarget(cmd.target() != null ? cmd.target() : existing.getTarget())
                .withVisibility(cmd.visibility() != null ? cmd.visibility() : existing.getVisibility())
                .withReminderHour(cmd.reminderHour()).withReminderMinute(cmd.reminderMinute())
                .withWeeklyDueDays(cmd.weeklyDueDays() != null ? cmd.weeklyDueDays() : existing.getWeeklyDueDays())
                .withMonthlyDueDay(cmd.monthlyDueDay() != null ? cmd.monthlyDueDay() : existing.getMonthlyDueDay())
                .withUpdatedAt(Instant.now()));
    }

    @Override
    public void deleteChallenge(UUID userId, UUID challengeId) {
        Challenge existing = challengeRepository.findByIdAndOwnerId(challengeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
        challengeRepository.save(existing.withActive(false).withUpdatedAt(Instant.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Challenge> getChallengesForMonth(UUID userId, YearMonth month) {
        return challengeRepository.findActiveByOwnerIdAndMonth(userId, month);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Challenge> getTodaysChallenges(UUID userId) {
        LocalDate today = LocalDate.now();
        return challengeRepository.findActiveByOwnerIdAndMonth(userId, YearMonth.from(today))
                .stream()
                .filter(c -> c.isDueOnDay(today.getDayOfMonth(), today.getDayOfWeek()))
                .toList();
    }

    @Override
    public List<Challenge> rolloverChallenges(UUID userId, YearMonth fromMonth, YearMonth toMonth) {
        return challengeRepository.findActiveByOwnerIdAndMonth(userId, fromMonth).stream()
                .map(c -> challengeRepository.save(c.withId(UUID.randomUUID()).withMonth(toMonth)
                        .withActive(true).withCreatedAt(Instant.now()).withUpdatedAt(Instant.now())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChallengeTemplate> getChallengeTemplates() {
        return List.of(
                tpl("Drink 3L Water",    ChallengeCategory.HEALTH,       ChallengeFrequency.DAILY, 3.0,  "Litres"),
                tpl("Sleep 7 Hours",     ChallengeCategory.SLEEP,        ChallengeFrequency.DAILY, 7.0,  "hours"),
                tpl("Walk 5 km",         ChallengeCategory.FITNESS,      ChallengeFrequency.DAILY, 5.0,  "km"),
                tpl("Read 10 Pages",     ChallengeCategory.LEARNING,     ChallengeFrequency.DAILY, 10.0, "pages"),
                tpl("Meditate 10 min",   ChallengeCategory.MINDFULNESS,  ChallengeFrequency.DAILY, 10.0, "minutes"),
                tpl("Exercise 30 min",   ChallengeCategory.FITNESS,      ChallengeFrequency.DAILY, 30.0, "minutes"),
                tpl("No Junk Food",      ChallengeCategory.NUTRITION,    ChallengeFrequency.DAILY, null, null)
        );
    }

    private ChallengeTemplate tpl(String title, ChallengeCategory cat, ChallengeFrequency freq,
                                   Double val, String unit) {
        return ChallengeTemplate.builder().id(UUID.randomUUID()).title(title).category(cat)
                .suggestedFrequency(freq)
                .suggestedTarget(val != null ? new ChallengeTarget(val, unit) : null)
                .build();
    }
}
