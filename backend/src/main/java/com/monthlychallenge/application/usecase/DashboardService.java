package com.monthlychallenge.application.usecase;

import com.monthlychallenge.adapter.out.persistence.challenge.ChallengeJpaRepository;
import com.monthlychallenge.adapter.out.persistence.checkin.CheckInJpaRepository;
import com.monthlychallenge.adapter.out.persistence.daysummary.DaySummaryJpaRepository;
import com.monthlychallenge.application.dto.ChallengeCompletionRate;
import com.monthlychallenge.domain.enums.DayResult;
import com.monthlychallenge.domain.models.DaySummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final DaySummaryJpaRepository daySummaryRepo;
    private final ChallengeJpaRepository challengeRepo;
    private final CheckInJpaRepository checkInRepo;

    public DashboardService(DaySummaryJpaRepository daySummaryRepo,
                            ChallengeJpaRepository challengeRepo,
                            CheckInJpaRepository checkInRepo) {
        this.daySummaryRepo = daySummaryRepo;
        this.challengeRepo = challengeRepo;
        this.checkInRepo = checkInRepo;
    }

    public List<DaySummary> getMonthlyCalendar(UUID userId, YearMonth month) {
        return daySummaryRepo.findByUserIdAndDateBetween(
                userId, month.atDay(1), month.atEndOfMonth()).stream()
                .map(ds -> DaySummary.builder()
                        .id(ds.getId()).userId(ds.getUserId()).date(ds.getDate())
                        .totalPoints(ds.getTotalPoints()).minimumThreshold(ds.getMinimumThreshold())
                        .result(DayResult.valueOf(ds.getResult()))
                        .build())
                .toList();
    }

    public List<ChallengeCompletionRate> getMonthlyCompletionRates(UUID userId, YearMonth month) {
        LocalDate from = month.atDay(1);
        LocalDate to = month.atEndOfMonth();
        var challenges = challengeRepo.findByOwnerIdAndMonthAndActiveTrue(userId, month.toString());

        List<Object[]> summaries = checkInRepo.summariseByChallenge(userId, from, to);
        Map<UUID, long[]> summaryMap = new HashMap<>();
        for (Object[] row : summaries) {
            UUID cId = (UUID) row[0];
            long comp = row[1] != null ? ((Number) row[1]).longValue() : 0;
            long half = row[2] != null ? ((Number) row[2]).longValue() : 0;
            long miss = row[3] != null ? ((Number) row[3]).longValue() : 0;
            summaryMap.put(cId, new long[]{comp, half, miss});
        }

        return challenges.stream().map(c -> {
            long[] s = summaryMap.getOrDefault(c.getId(), new long[]{0, 0, 0});
            int total = (int) (s[0] + s[1] + s[2]);
            return new ChallengeCompletionRate(c.getId(), c.getTitle(), total,
                    (int) s[0], (int) s[1], (int) s[2]);
        }).toList();
    }
}
