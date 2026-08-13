package com.monthlychallenge.application.usecase;

import com.monthlychallenge.application.ports.in.ChallengeUseCase;
import com.monthlychallenge.application.ports.in.command.CreateChallengeCommand;
import com.monthlychallenge.application.ports.in.command.UpdateChallengeCommand;
import com.monthlychallenge.application.ports.out.ChallengeRepository;
import com.monthlychallenge.domain.enums.ChallengeCategory;
import com.monthlychallenge.domain.enums.ChallengeFrequency;
import com.monthlychallenge.domain.enums.ChallengeVisibility;
import com.monthlychallenge.domain.exceptions.ResourceNotFoundException;
import com.monthlychallenge.domain.models.Challenge;
import com.monthlychallenge.domain.models.ChallengeTarget;
import com.monthlychallenge.domain.models.ChallengeTemplate;
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
        Challenge c = Challenge.builder()
                .id(UUID.randomUUID())
                .ownerId(userId)
                .title(cmd.title())
                .description(cmd.description())
                .category(cmd.category())
                .frequency(cmd.frequency())
                .target(cmd.target())
                .month(cmd.month() != null ? cmd.month() : YearMonth.now())
                .visibility(cmd.visibility() != null ? cmd.visibility() : ChallengeVisibility.SHARED)
                .reminderHour(cmd.reminderHour())
                .reminderMinute(cmd.reminderMinute())
                .weeklyDueDays(cmd.weeklyDueDays())
                .monthlyDueDay(cmd.monthlyDueDay())
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return challengeRepository.save(c);
    }

    @Override
    public Challenge updateChallenge(UUID userId, UUID challengeId, UpdateChallengeCommand cmd) {
        Challenge existing = challengeRepository.findByIdAndOwnerId(challengeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
        if (cmd.title() != null)       existing.setTitle(cmd.title());
        if (cmd.description() != null) existing.setDescription(cmd.description());
        if (cmd.category() != null)    existing.setCategory(cmd.category());
        if (cmd.target() != null)      existing.setTarget(cmd.target());
        if (cmd.visibility() != null)  existing.setVisibility(cmd.visibility());
        if (cmd.reminderHour() != null)   existing.setReminderHour(cmd.reminderHour());
        if (cmd.reminderMinute() != null) existing.setReminderMinute(cmd.reminderMinute());
        if (cmd.weeklyDueDays() != null)  existing.setWeeklyDueDays(cmd.weeklyDueDays());
        if (cmd.monthlyDueDay() != null)  existing.setMonthlyDueDay(cmd.monthlyDueDay());
        existing.setUpdatedAt(Instant.now());
        return challengeRepository.save(existing);
    }

    @Override
    public void deleteChallenge(UUID userId, UUID challengeId) {
        Challenge existing = challengeRepository.findByIdAndOwnerId(challengeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
        existing.setActive(false);
        existing.setUpdatedAt(Instant.now());
        challengeRepository.save(existing);
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
                .map(c -> challengeRepository.save(Challenge.builder()
                        .id(UUID.randomUUID())
                        .ownerId(c.getOwnerId())
                        .title(c.getTitle())
                        .description(c.getDescription())
                        .category(c.getCategory())
                        .frequency(c.getFrequency())
                        .target(c.getTarget())
                        .month(toMonth)
                        .visibility(c.getVisibility())
                        .reminderHour(c.getReminderHour())
                        .reminderMinute(c.getReminderMinute())
                        .weeklyDueDays(c.getWeeklyDueDays())
                        .monthlyDueDay(c.getMonthlyDueDay())
                        .active(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()))
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
