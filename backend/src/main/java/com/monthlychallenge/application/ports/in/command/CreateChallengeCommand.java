package com.monthlychallenge.application.ports.in.command;

import com.monthlychallenge.domain.enums.ChallengeCategory;
import com.monthlychallenge.domain.enums.ChallengeFrequency;
import com.monthlychallenge.domain.enums.ChallengeVisibility;
import com.monthlychallenge.domain.models.ChallengeTarget;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.Set;

public record CreateChallengeCommand(
        String title,
        String description,
        ChallengeCategory category,
        ChallengeFrequency frequency,
        ChallengeTarget target,
        YearMonth month,
        ChallengeVisibility visibility,
        Integer reminderHour,
        Integer reminderMinute,
        Set<DayOfWeek> weeklyDueDays,
        Integer monthlyDueDay
) {}
