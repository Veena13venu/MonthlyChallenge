package com.monthlychallenge.application.ports.in;

import com.monthlychallenge.application.dto.ChallengeCompletionRate;
import com.monthlychallenge.domain.models.DaySummary;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public interface DashboardUseCase {
    List<DaySummary> getMonthlyCalendar(UUID userId, YearMonth month);
    List<ChallengeCompletionRate> getMonthlyCompletionRates(UUID userId, YearMonth month);
}
