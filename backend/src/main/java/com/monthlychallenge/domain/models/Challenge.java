package com.monthlychallenge.domain.models;

import com.monthlychallenge.domain.enums.ChallengeCategory;
import com.monthlychallenge.domain.enums.ChallengeFrequency;
import com.monthlychallenge.domain.enums.ChallengeVisibility;
import lombok.*;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Challenge {
    private UUID id;
    private UUID ownerId;
    private String title;
    private String description;
    private ChallengeCategory category;
    private ChallengeFrequency frequency;
    private ChallengeTarget target;
    private YearMonth month;
    private ChallengeVisibility visibility;
    private Integer reminderHour;
    private Integer reminderMinute;
    private Set<DayOfWeek> weeklyDueDays;
    private Integer monthlyDueDay;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean isDueOnDay(int dayOfMonth, DayOfWeek dayOfWeek) {
        return switch (frequency) {
            case DAILY -> true;
            case WEEKLY -> weeklyDueDays != null && weeklyDueDays.contains(dayOfWeek);
            case MONTHLY -> monthlyDueDay != null && monthlyDueDay == dayOfMonth;
        };
    }
}
