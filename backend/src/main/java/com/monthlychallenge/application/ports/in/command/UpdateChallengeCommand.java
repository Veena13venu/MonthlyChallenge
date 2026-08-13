package com.monthlychallenge.application.ports.in.command;

import com.monthlychallenge.domain.enums.ChallengeCategory;
import com.monthlychallenge.domain.enums.ChallengeVisibility;
import com.monthlychallenge.domain.models.ChallengeTarget;

import java.time.DayOfWeek;
import java.util.Set;

public record UpdateChallengeCommand(
        String title,
        String description,
        ChallengeCategory category,
        ChallengeTarget target,
        ChallengeVisibility visibility,
        Integer reminderHour,
        Integer reminderMinute,
        Set<DayOfWeek> weeklyDueDays,
        Integer monthlyDueDay
) {}
