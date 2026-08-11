package com.monthlychallenge.domain.service;

import com.monthlychallenge.domain.models.*;
import com.monthlychallenge.domain.enums.DayResult;
import com.monthlychallenge.domain.enums.CheckInStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class DaySummaryDomainService {

    public DaySummary computeDaySummary(UUID userId, LocalDate date, List<CheckIn> checkIns,
                                         int totalDue, MinimumDailyTarget minimumTarget) {
        double totalPoints = checkIns.stream().mapToDouble(CheckIn::pointValue).sum();
        double threshold = minimumTarget.resolveThreshold(totalDue);
        DayResult result = DaySummary.evaluate(totalPoints, threshold);
        return DaySummary.builder()
                .id(UUID.randomUUID()).userId(userId).date(date)
                .totalPoints(totalPoints).minimumThreshold(threshold).result(result)
                .build();
    }

    public Streak updateStreak(Streak streak, DaySummary daySummary) {
        return streak.applyDayResult(daySummary.getResult(), daySummary.getDate());
    }
}
