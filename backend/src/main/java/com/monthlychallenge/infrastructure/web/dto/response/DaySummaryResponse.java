package com.monthlychallenge.infrastructure.web.dto.response;

import com.monthlychallenge.domain.model.DayResult;

import java.time.LocalDate;

public record DaySummaryResponse(
        LocalDate date,
        double totalPoints,
        double minimumThreshold,
        DayResult result
) {}
