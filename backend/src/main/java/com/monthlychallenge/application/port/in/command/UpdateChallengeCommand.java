package com.monthlychallenge.application.port.in.command;

import com.monthlychallenge.domain.model.*;
import java.time.DayOfWeek;
import java.util.Set;

public record UpdateChallengeCommand(
        String title, String description, ChallengeCategory category,
        ChallengeTarget target, ChallengeVisibility visibility,
        Integer reminderHour, Integer reminderMinute,
        Set<DayOfWeek> weeklyDueDays, Integer monthlyDueDay) {}
