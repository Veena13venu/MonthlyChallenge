package com.monthlychallenge.application.usecase;

import com.monthlychallenge.adapter.out.persistence.challenge.ChallengeJpaEntity;
import com.monthlychallenge.adapter.out.persistence.challenge.ChallengeJpaRepository;
import com.monthlychallenge.adapter.out.persistence.checkin.CheckInJpaEntity;
import com.monthlychallenge.adapter.out.persistence.checkin.CheckInJpaRepository;
import com.monthlychallenge.adapter.out.persistence.daysummary.DaySummaryJpaEntity;
import com.monthlychallenge.adapter.out.persistence.daysummary.DaySummaryJpaRepository;
import com.monthlychallenge.adapter.out.persistence.streak.StreakJpaEntity;
import com.monthlychallenge.adapter.out.persistence.streak.StreakJpaRepository;
import com.monthlychallenge.adapter.out.persistence.user.UserJpaEntity;
import com.monthlychallenge.adapter.out.persistence.user.UserJpaRepository;
import com.monthlychallenge.domain.enums.CheckInStatus;
import com.monthlychallenge.domain.models.CheckIn;
import com.monthlychallenge.domain.models.DaySummary;
import com.monthlychallenge.domain.models.MinimumDailyTarget;
import com.monthlychallenge.domain.models.Streak;
import com.monthlychallenge.domain.service.DaySummaryDomainService;
import com.monthlychallenge.infrastructure.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class EndOfDaySchedulerService {

    private static final Logger log = LoggerFactory.getLogger(EndOfDaySchedulerService.class);

    private final UserJpaRepository userRepo;
    private final ChallengeJpaRepository challengeRepo;
    private final CheckInJpaRepository checkInRepo;
    private final DaySummaryJpaRepository daySummaryRepo;
    private final StreakJpaRepository streakRepo;
    private final NotificationService notificationService;
    private final DaySummaryDomainService daySummaryDomainService;

    public EndOfDaySchedulerService(UserJpaRepository userRepo,
                                     ChallengeJpaRepository challengeRepo,
                                     CheckInJpaRepository checkInRepo,
                                     DaySummaryJpaRepository daySummaryRepo,
                                     StreakJpaRepository streakRepo,
                                     NotificationService notificationService,
                                     DaySummaryDomainService daySummaryDomainService) {
        this.userRepo = userRepo;
        this.challengeRepo = challengeRepo;
        this.checkInRepo = checkInRepo;
        this.daySummaryRepo = daySummaryRepo;
        this.streakRepo = streakRepo;
        this.notificationService = notificationService;
        this.daySummaryDomainService = daySummaryDomainService;
    }

    @Scheduled(cron = "${app.scheduler.end-of-day-cron}", zone = "${app.scheduler.timezone}")
    @Transactional
    public void runEndOfDay() {
        LocalDate today = LocalDate.now();
        log.info("Running end-of-day job for {}", today);
        userRepo.findAll().forEach(user -> processUser(user, today));
        log.info("End-of-day job complete for {}", today);
    }

    private void processUser(UserJpaEntity user, LocalDate today) {
        try {
            UUID userId = user.getId();
            String month = YearMonth.from(today).toString();

            List<ChallengeJpaEntity> due = challengeRepo.findByOwnerIdAndMonthAndActiveTrue(userId, month).stream()
                    .filter(c -> isDueOnDay(c.getFrequency(), c.getWeeklyDueDays(), c.getMonthlyDueDay(), today))
                    .toList();
            if (due.isEmpty()) return;

            List<CheckInJpaEntity> existing = checkInRepo.findByUserIdAndDate(userId, today);
            for (ChallengeJpaEntity c : due) {
                boolean checked = existing.stream().anyMatch(ci -> ci.getChallengeId().equals(c.getId()));
                if (!checked) {
                    CheckInJpaEntity missed = new CheckInJpaEntity();
                    missed.setId(UUID.randomUUID());
                    missed.setUserId(userId);
                    missed.setChallengeId(c.getId());
                    missed.setDate(today);
                    missed.setStatus(CheckInStatus.MISSED.name());
                    missed.setCreatedAt(Instant.now());
                    missed.setUpdatedAt(Instant.now());
                    checkInRepo.save(missed);
                }
            }

            List<CheckIn> allCheckIns = checkInRepo.findByUserIdAndDate(userId, today).stream()
                    .map(ci -> CheckIn.builder()
                            .id(ci.getId()).userId(ci.getUserId()).challengeId(ci.getChallengeId())
                            .date(ci.getDate()).status(CheckInStatus.valueOf(ci.getStatus()))
                            .actualValue(ci.getActualValue()).build())
                    .toList();

            MinimumDailyTarget target = new MinimumDailyTarget(
                    user.getMinimumTargetValue(), user.isMinimumTargetIsPercentage());

            DaySummary summary = daySummaryDomainService.computeDaySummary(
                    userId, today, allCheckIns, due.size(), target);

            DaySummaryJpaEntity dsEntity = new DaySummaryJpaEntity();
            dsEntity.setId(UUID.randomUUID());
            dsEntity.setUserId(userId);
            dsEntity.setDate(today);
            dsEntity.setTotalPoints(summary.getTotalPoints());
            dsEntity.setMinimumThreshold(summary.getMinimumThreshold());
            dsEntity.setResult(summary.getResult().name());
            daySummaryRepo.save(dsEntity);

            StreakJpaEntity sEntity = streakRepo.findByUserId(userId).orElseGet(() -> {
                StreakJpaEntity e = new StreakJpaEntity();
                e.setId(UUID.randomUUID());
                e.setUserId(userId);
                e.setCurrentStreak(0);
                e.setLongestStreak(0);
                return e;
            });

            Streak current = Streak.builder()
                    .id(sEntity.getId()).userId(sEntity.getUserId())
                    .currentStreak(sEntity.getCurrentStreak()).longestStreak(sEntity.getLongestStreak())
                    .lastSuccessDate(sEntity.getLastSuccessDate()).build();

            Streak updated = daySummaryDomainService.updateStreak(current, summary);
            sEntity.setCurrentStreak(updated.getCurrentStreak());
            sEntity.setLongestStreak(updated.getLongestStreak());
            sEntity.setLastSuccessDate(updated.getLastSuccessDate());
            streakRepo.save(sEntity);

        } catch (Exception e) {
            log.error("End-of-day processing failed for user {}: {}", user.getId(), e.getMessage(), e);
        }
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
