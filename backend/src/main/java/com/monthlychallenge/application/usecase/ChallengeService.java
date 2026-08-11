package com.monthlychallenge.application.usecase;

import com.monthlychallenge.adapter.in.rest.dto.request.CreateChallengeRequest;
import com.monthlychallenge.adapter.in.rest.dto.request.UpdateChallengeRequest;
import com.monthlychallenge.adapter.out.persistence.challenge.ChallengeJpaEntity;
import com.monthlychallenge.adapter.out.persistence.challenge.ChallengeJpaRepository;
import com.monthlychallenge.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChallengeService {

    private final ChallengeJpaRepository challengeRepo;

    public ChallengeService(ChallengeJpaRepository challengeRepo) {
        this.challengeRepo = challengeRepo;
    }

    public ChallengeJpaEntity createChallenge(UUID userId, CreateChallengeRequest req) {
        ChallengeJpaEntity c = new ChallengeJpaEntity();
        c.setId(UUID.randomUUID());
        c.setOwnerId(userId);
        c.setTitle(req.getTitle());
        c.setDescription(req.getDescription());
        c.setCategory(req.getCategory() != null ? req.getCategory().name() : null);
        c.setFrequency(req.getFrequency() != null ? req.getFrequency().name() : null);
        c.setMonth(req.getMonth() != null ? req.getMonth().toString() : YearMonth.now().toString());
        c.setVisibility(req.getVisibility() != null ? req.getVisibility().name() : "SHARED");

        String targetStr = null;
        if (req.getTargetValue() != null) {
            targetStr = req.getTargetUnit() != null && !req.getTargetUnit().isBlank()
                    ? req.getTargetValue() + ":" + req.getTargetUnit()
                    : String.valueOf(req.getTargetValue());
        }
        c.setTargetValue(targetStr);

        c.setReminderHour(req.getReminderHour());
        c.setReminderMinute(req.getReminderMinute());
        c.setWeeklyDueDays(req.getWeeklyDueDays() != null ? req.getWeeklyDueDays().stream().map(Enum::name).collect(Collectors.joining(",")) : null);
        c.setMonthlyDueDay(req.getMonthlyDueDay());
        c.setActive(true);
        c.setCreatedAt(Instant.now());
        c.setUpdatedAt(Instant.now());
        return challengeRepo.save(c);
    }

    public ChallengeJpaEntity updateChallenge(UUID userId, UUID challengeId, UpdateChallengeRequest req) {
        ChallengeJpaEntity c = challengeRepo.findByIdAndOwnerIdAndActiveTrue(challengeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
        if (req.getTitle() != null)       c.setTitle(req.getTitle());
        if (req.getDescription() != null) c.setDescription(req.getDescription());
        if (req.getCategory() != null)    c.setCategory(req.getCategory().name());
        if (req.getVisibility() != null)  c.setVisibility(req.getVisibility().name());

        if (req.getTargetValue() != null) {
            String targetStr = req.getTargetUnit() != null && !req.getTargetUnit().isBlank()
                    ? req.getTargetValue() + ":" + req.getTargetUnit()
                    : String.valueOf(req.getTargetValue());
            c.setTargetValue(targetStr);
        }

        if (req.getReminderHour() != null)   c.setReminderHour(req.getReminderHour());
        if (req.getReminderMinute() != null) c.setReminderMinute(req.getReminderMinute());
        if (req.getWeeklyDueDays() != null)  c.setWeeklyDueDays(req.getWeeklyDueDays().stream().map(Enum::name).collect(Collectors.joining(",")));
        if (req.getMonthlyDueDay() != null)  c.setMonthlyDueDay(req.getMonthlyDueDay());
        c.setUpdatedAt(Instant.now());
        return challengeRepo.save(c);
    }

    public void deleteChallenge(UUID userId, UUID challengeId) {
        ChallengeJpaEntity c = challengeRepo.findByIdAndOwnerIdAndActiveTrue(challengeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
        c.setActive(false);
        c.setUpdatedAt(Instant.now());
        challengeRepo.save(c);
    }

    @Transactional(readOnly = true)
    public List<ChallengeJpaEntity> getChallengesForMonth(UUID userId, String month) {
        return challengeRepo.findByOwnerIdAndMonthAndActiveTrue(userId, month);
    }

    @Transactional(readOnly = true)
    public List<ChallengeJpaEntity> getTodaysChallenges(UUID userId) {
        LocalDate today = LocalDate.now();
        String month = YearMonth.from(today).toString();
        return challengeRepo.findByOwnerIdAndMonthAndActiveTrue(userId, month)
                .stream()
                .filter(c -> isDueToday(c, today))
                .toList();
    }

    public List<ChallengeJpaEntity> rolloverChallenges(UUID userId, String fromMonth, String toMonth) {
        return challengeRepo.findByOwnerIdAndMonthAndActiveTrue(userId, fromMonth).stream()
                .map(c -> {
                    ChallengeJpaEntity copy = new ChallengeJpaEntity();
                    copy.setId(UUID.randomUUID());
                    copy.setOwnerId(c.getOwnerId());
                    copy.setTitle(c.getTitle());
                    copy.setDescription(c.getDescription());
                    copy.setCategory(c.getCategory());
                    copy.setFrequency(c.getFrequency());
                    copy.setTargetValue(c.getTargetValue());
                    copy.setMonth(toMonth);
                    copy.setVisibility(c.getVisibility());
                    copy.setReminderHour(c.getReminderHour());
                    copy.setReminderMinute(c.getReminderMinute());
                    copy.setWeeklyDueDays(c.getWeeklyDueDays());
                    copy.setMonthlyDueDay(c.getMonthlyDueDay());
                    copy.setActive(true);
                    copy.setCreatedAt(Instant.now());
                    copy.setUpdatedAt(Instant.now());
                    return challengeRepo.save(copy);
                }).toList();
    }

    private boolean isDueToday(ChallengeJpaEntity c, LocalDate today) {
        return switch (c.getFrequency()) {
            case "DAILY"   -> true;
            case "WEEKLY"  -> c.getWeeklyDueDays() != null &&
                              c.getWeeklyDueDays().contains(today.getDayOfWeek().name());
            case "MONTHLY" -> c.getMonthlyDueDay() != null &&
                              c.getMonthlyDueDay() == today.getDayOfMonth();
            default        -> false;
        };
    }
}
