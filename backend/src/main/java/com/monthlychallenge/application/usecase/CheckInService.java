package com.monthlychallenge.application.usecase;

import com.monthlychallenge.adapter.out.persistence.checkin.CheckInJpaEntity;
import com.monthlychallenge.adapter.out.persistence.checkin.CheckInJpaRepository;
import com.monthlychallenge.adapter.out.persistence.user.UserJpaRepository;
import com.monthlychallenge.adapter.out.persistence.challenge.ChallengeJpaRepository;
import com.monthlychallenge.domain.enums.CheckInStatus;
import com.monthlychallenge.domain.models.CheckIn;
import com.monthlychallenge.domain.models.DaySummary;
import com.monthlychallenge.domain.models.MinimumDailyTarget;
import com.monthlychallenge.domain.service.DaySummaryDomainService;
import com.monthlychallenge.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CheckInService {

    private final CheckInJpaRepository checkInRepo;
    private final UserJpaRepository userRepo;
    private final ChallengeJpaRepository challengeRepo;
    private final DaySummaryDomainService daySummaryDomainService;

    public CheckInService(CheckInJpaRepository checkInRepo,
                          UserJpaRepository userRepo,
                          ChallengeJpaRepository challengeRepo,
                          DaySummaryDomainService daySummaryDomainService) {
        this.checkInRepo = checkInRepo;
        this.userRepo = userRepo;
        this.challengeRepo = challengeRepo;
        this.daySummaryDomainService = daySummaryDomainService;
    }

    public CheckInJpaEntity recordCheckIn(UUID userId, UUID challengeId, CheckInStatus status, Double actualValue) {
        LocalDate today = LocalDate.now();

        CheckInJpaEntity ci = checkInRepo
                .findByUserIdAndChallengeIdAndDate(userId, challengeId, today)
                .orElseGet(() -> {
                    CheckInJpaEntity entity = new CheckInJpaEntity();
                    entity.setId(UUID.randomUUID());
                    entity.setUserId(userId);
                    entity.setChallengeId(challengeId);
                    entity.setDate(today);
                    entity.setCreatedAt(Instant.now());
                    return entity;
                });

        ci.setStatus(status.name());
        ci.setActualValue(actualValue);
        ci.setUpdatedAt(Instant.now());
        return checkInRepo.save(ci);
    }

    @Transactional(readOnly = true)
    public List<CheckInJpaEntity> getCheckInsForDate(UUID userId, LocalDate date) {
        return checkInRepo.findByUserIdAndDate(userId, date);
    }

    @Transactional(readOnly = true)
    public DaySummary getLiveDaySummary(UUID userId, LocalDate date) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<CheckIn> checkIns = checkInRepo.findByUserIdAndDate(userId, date).stream()
                .map(ci -> CheckIn.builder()
                        .id(ci.getId()).userId(ci.getUserId()).challengeId(ci.getChallengeId())
                        .date(ci.getDate()).status(CheckInStatus.valueOf(ci.getStatus()))
                        .actualValue(ci.getActualValue()).build())
                .toList();

        String month = YearMonth.from(date).toString();
        int totalDue = (int) challengeRepo.findByOwnerIdAndMonthAndActiveTrue(userId, month).stream()
                .filter(c -> isDueOnDay(c.getFrequency(), c.getWeeklyDueDays(), c.getMonthlyDueDay(), date))
                .count();

        MinimumDailyTarget target = new MinimumDailyTarget(
                user.getMinimumTargetValue(), user.isMinimumTargetIsPercentage());

        return daySummaryDomainService.computeDaySummary(userId, date, checkIns, totalDue, target);
    }

    private boolean isDueOnDay(String frequency, String weeklyDueDays, Integer monthlyDueDay, LocalDate date) {
        return switch (frequency) {
            case "DAILY" -> true;
            case "WEEKLY" -> weeklyDueDays != null && weeklyDueDays.contains(date.getDayOfWeek().name());
            case "MONTHLY" -> monthlyDueDay != null && monthlyDueDay == date.getDayOfMonth();
            default -> false;
        };
    }
}
