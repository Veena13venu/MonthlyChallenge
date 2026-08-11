package com.monthlychallenge.application.dto;

import com.monthlychallenge.domain.enums.DayResult;

import java.time.LocalDate;

public record DaySummaryResponse(
        LocalDate date,
        double totalPoints,
        double minimumThreshold,
        DayResult result
) {}
