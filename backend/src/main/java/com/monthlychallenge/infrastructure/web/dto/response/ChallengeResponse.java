package com.monthlychallenge.infrastructure.web.dto.response;

import com.monthlychallenge.domain.model.ChallengeCategory;
import com.monthlychallenge.domain.model.ChallengeFrequency;
import com.monthlychallenge.domain.model.ChallengeVisibility;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;

public record ChallengeResponse(
        UUID id,
        String title,
        String description,
        ChallengeCategory category,
        ChallengeFrequency frequency,
        YearMonth month,
        ChallengeVisibility visibility,
        Double targetValue,
        String targetUnit,
        Integer reminderHour,
        Integer reminderMinute,
        Set<DayOfWeek> weeklyDueDays,
        Integer monthlyDueDay,
        boolean active
) {}
