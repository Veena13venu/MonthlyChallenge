package com.monthlychallenge.application.port.in;

import com.monthlychallenge.domain.model.DaySummary;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

/**
 * Inbound port — progress dashboard and history (FR-36, FR-37, FR-38).
 */
public interface DashboardUseCase {

    /** Monthly calendar view: a DaySummary for every day of the given month. */
    List<DaySummary> getMonthlyCalendar(UUID userId, YearMonth month);

    /** Per-challenge completion rate for the given month (FR-37). */
    List<com.monthlychallenge.application.dto.ChallengeCompletionRate> getMonthlyCompletionRates(UUID userId, YearMonth month);
}
