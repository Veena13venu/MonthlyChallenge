package com.monthlychallenge.application.service;

import com.monthlychallenge.application.dto.ChallengeCheckInSummary;
import com.monthlychallenge.application.dto.ChallengeCompletionRate;
import com.monthlychallenge.application.port.in.DashboardUseCase;
import com.monthlychallenge.application.port.out.ChallengeRepository;
import com.monthlychallenge.application.port.out.CheckInRepository;
import com.monthlychallenge.application.port.out.DaySummaryRepository;
import com.monthlychallenge.domain.model.DaySummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService implements DashboardUseCase {

    private final DaySummaryRepository daySummaryRepository;
    private final ChallengeRepository challengeRepository;
    private final CheckInRepository checkInRepository;

    public DashboardService(DaySummaryRepository daySummaryRepository,
                             ChallengeRepository challengeRepository,
                             CheckInRepository checkInRepository) {
        this.daySummaryRepository = daySummaryRepository;
        this.challengeRepository = challengeRepository;
        this.checkInRepository = checkInRepository;
    }

    @Override
    public List<DaySummary> getMonthlyCalendar(UUID userId, YearMonth month) {
        return daySummaryRepository.findByUserIdAndDateBetween(
                userId, month.atDay(1), month.atEndOfMonth());
    }

    @Override
    public List<ChallengeCompletionRate> getMonthlyCompletionRates(UUID userId, YearMonth month) {
        LocalDate from = month.atDay(1);
        LocalDate to   = month.atEndOfMonth();
        var challenges = challengeRepository.findActiveByOwnerIdAndMonth(userId, month);
        Map<UUID, ChallengeCheckInSummary> summaryMap = checkInRepository
                .summariseByChallenge(userId, from, to).stream()
                .collect(Collectors.toMap(ChallengeCheckInSummary::challengeId, s -> s));

        return challenges.stream().map(c -> {
            ChallengeCheckInSummary s = summaryMap.getOrDefault(c.getId(),
                    new ChallengeCheckInSummary(c.getId(), 0, 0, 0));
            int total = (int)(s.completedCount() + s.halfCompletedCount() + s.missedCount());
            return new ChallengeCompletionRate(c.getId(), c.getTitle(), total,
                    (int) s.completedCount(), (int) s.halfCompletedCount(), (int) s.missedCount());
        }).toList();
    }
}
