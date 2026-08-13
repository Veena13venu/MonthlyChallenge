package com.monthlychallenge.application.usecase;

import com.monthlychallenge.application.ports.out.*;
import com.monthlychallenge.domain.enums.CheckInStatus;
import com.monthlychallenge.domain.enums.DayResult;
import com.monthlychallenge.domain.models.*;
import com.monthlychallenge.domain.service.DaySummaryDomainService;
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

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final CheckInRepository checkInRepository;
    private final DaySummaryRepository daySummaryRepository;
    private final StreakRepository streakRepository;
    private final NotificationPort notificationPort;
    private final DaySummaryDomainService daySummaryDomainService;

    public EndOfDaySchedulerService(UserRepository userRepository,
                                     ChallengeRepository challengeRepository,
                                     CheckInRepository checkInRepository,
                                     DaySummaryRepository daySummaryRepository,
                                     StreakRepository streakRepository,
                                     NotificationPort notificationPort,
                                     DaySummaryDomainService daySummaryDomainService) {
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.checkInRepository = checkInRepository;
        this.daySummaryRepository = daySummaryRepository;
        this.streakRepository = streakRepository;
        this.notificationPort = notificationPort;
        this.daySummaryDomainService = daySummaryDomainService;
    }

    @Scheduled(cron = "${app.scheduler.end-of-day-cron}", zone = "${app.scheduler.timezone}")
    @Transactional
    public void runEndOfDay() {
        LocalDate today = LocalDate.now();
        log.info("Running end-of-day job for {}", today);
        userRepository.searchByUsernameStartingWith("", null)
                .forEach(user -> processUser(user, today));
        log.info("End-of-day job complete for {}", today);
    }

    private void processUser(User user, LocalDate today) {
        try {
            UUID userId = user.getId();
            YearMonth month = YearMonth.from(today);

            List<Challenge> due = challengeRepository.findActiveByOwnerIdAndMonth(userId, month).stream()
                    .filter(c -> c.isDueOnDay(today.getDayOfMonth(), today.getDayOfWeek()))
                    .toList();
            if (due.isEmpty()) return;

            List<CheckIn> existing = checkInRepository.findByUserIdAndDate(userId, today);
            for (Challenge c : due) {
                boolean checked = existing.stream().anyMatch(ci -> ci.getChallengeId().equals(c.getId()));
                if (!checked) {
                    checkInRepository.save(CheckIn.builder()
                            .id(UUID.randomUUID())
                            .userId(userId)
                            .challengeId(c.getId())
                            .date(today)
                            .status(CheckInStatus.MISSED)
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build());
                }
            }

            List<CheckIn> allCheckIns = checkInRepository.findByUserIdAndDate(userId, today);
            DaySummary summary = daySummaryDomainService.computeDaySummary(
                    userId, today, allCheckIns, due.size(), user.getMinimumDailyTarget());
            daySummaryRepository.save(summary);

            Streak current = streakRepository.findByUserId(userId).orElseGet(() ->
                    Streak.builder().id(UUID.randomUUID()).userId(userId)
                            .currentStreak(0).longestStreak(0).build());

            Streak updated = daySummaryDomainService.updateStreak(current, summary);
            streakRepository.save(updated);

            if (summary.getResult() != DayResult.SUCCESS)
                notificationPort.sendEndOfDaySummary(userId, summary.getTotalPoints(), summary.getMinimumThreshold());

            int streak = updated.getCurrentStreak();
            if (streak == 7 || streak == 30 || streak == 100)
                notificationPort.sendStreakMilestone(userId, streak);

        } catch (Exception e) {
            log.error("End-of-day processing failed for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }
}
