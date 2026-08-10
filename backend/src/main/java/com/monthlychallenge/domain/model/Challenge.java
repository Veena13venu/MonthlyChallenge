package com.monthlychallenge.domain.model;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;

public final class Challenge {

    private final UUID id;
    private final UUID ownerId;
    private final String title;
    private final String description;
    private final ChallengeCategory category;
    private final ChallengeFrequency frequency;
    private final ChallengeTarget target;
    private final YearMonth month;
    private final ChallengeVisibility visibility;
    private final Integer reminderHour;
    private final Integer reminderMinute;
    private final boolean active;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Set<DayOfWeek> weeklyDueDays;
    private final Integer monthlyDueDay;

    private Challenge(Builder b) {
        this.id = b.id; this.ownerId = b.ownerId; this.title = b.title;
        this.description = b.description; this.category = b.category;
        this.frequency = b.frequency; this.target = b.target; this.month = b.month;
        this.visibility = b.visibility; this.reminderHour = b.reminderHour;
        this.reminderMinute = b.reminderMinute; this.active = b.active;
        this.createdAt = b.createdAt; this.updatedAt = b.updatedAt;
        this.weeklyDueDays = b.weeklyDueDays; this.monthlyDueDay = b.monthlyDueDay;
    }

    public UUID getId()                        { return id; }
    public UUID getOwnerId()                   { return ownerId; }
    public String getTitle()                   { return title; }
    public String getDescription()             { return description; }
    public ChallengeCategory getCategory()     { return category; }
    public ChallengeFrequency getFrequency()   { return frequency; }
    public ChallengeTarget getTarget()         { return target; }
    public YearMonth getMonth()                { return month; }
    public ChallengeVisibility getVisibility() { return visibility; }
    public Integer getReminderHour()           { return reminderHour; }
    public Integer getReminderMinute()         { return reminderMinute; }
    public boolean isActive()                  { return active; }
    public Instant getCreatedAt()              { return createdAt; }
    public Instant getUpdatedAt()              { return updatedAt; }
    public Set<DayOfWeek> getWeeklyDueDays()   { return weeklyDueDays; }
    public Integer getMonthlyDueDay()          { return monthlyDueDay; }

    public boolean isDueOnDay(int dayOfMonth, DayOfWeek dayOfWeek) {
        return switch (frequency) {
            case DAILY   -> true;
            case WEEKLY  -> weeklyDueDays != null && weeklyDueDays.contains(dayOfWeek);
            case MONTHLY -> monthlyDueDay != null && monthlyDueDay == dayOfMonth;
        };
    }

    public Challenge withId(UUID v)                    { return toBuilder().id(v).build(); }
    public Challenge withTitle(String v)               { return toBuilder().title(v).build(); }
    public Challenge withDescription(String v)         { return toBuilder().description(v).build(); }
    public Challenge withCategory(ChallengeCategory v) { return toBuilder().category(v).build(); }
    public Challenge withTarget(ChallengeTarget v)     { return toBuilder().target(v).build(); }
    public Challenge withVisibility(ChallengeVisibility v) { return toBuilder().visibility(v).build(); }
    public Challenge withReminderHour(Integer v)       { return toBuilder().reminderHour(v).build(); }
    public Challenge withReminderMinute(Integer v)     { return toBuilder().reminderMinute(v).build(); }
    public Challenge withWeeklyDueDays(Set<DayOfWeek> v) { return toBuilder().weeklyDueDays(v).build(); }
    public Challenge withMonthlyDueDay(Integer v)      { return toBuilder().monthlyDueDay(v).build(); }
    public Challenge withActive(boolean v)             { return toBuilder().active(v).build(); }
    public Challenge withMonth(YearMonth v)            { return toBuilder().month(v).build(); }
    public Challenge withCreatedAt(Instant v)          { return toBuilder().createdAt(v).build(); }
    public Challenge withUpdatedAt(Instant v)          { return toBuilder().updatedAt(v).build(); }

    public static Builder builder() { return new Builder(); }
    private Builder toBuilder() {
        return new Builder().id(id).ownerId(ownerId).title(title).description(description)
                .category(category).frequency(frequency).target(target).month(month)
                .visibility(visibility).reminderHour(reminderHour).reminderMinute(reminderMinute)
                .active(active).createdAt(createdAt).updatedAt(updatedAt)
                .weeklyDueDays(weeklyDueDays).monthlyDueDay(monthlyDueDay);
    }

    public static final class Builder {
        private UUID id; private UUID ownerId; private String title; private String description;
        private ChallengeCategory category; private ChallengeFrequency frequency;
        private ChallengeTarget target; private YearMonth month; private ChallengeVisibility visibility;
        private Integer reminderHour; private Integer reminderMinute; private boolean active;
        private Instant createdAt; private Instant updatedAt;
        private Set<DayOfWeek> weeklyDueDays; private Integer monthlyDueDay;

        public Builder id(UUID v)                      { this.id = v; return this; }
        public Builder ownerId(UUID v)                 { this.ownerId = v; return this; }
        public Builder title(String v)                 { this.title = v; return this; }
        public Builder description(String v)           { this.description = v; return this; }
        public Builder category(ChallengeCategory v)   { this.category = v; return this; }
        public Builder frequency(ChallengeFrequency v) { this.frequency = v; return this; }
        public Builder target(ChallengeTarget v)       { this.target = v; return this; }
        public Builder month(YearMonth v)              { this.month = v; return this; }
        public Builder visibility(ChallengeVisibility v){ this.visibility = v; return this; }
        public Builder reminderHour(Integer v)         { this.reminderHour = v; return this; }
        public Builder reminderMinute(Integer v)       { this.reminderMinute = v; return this; }
        public Builder active(boolean v)               { this.active = v; return this; }
        public Builder createdAt(Instant v)            { this.createdAt = v; return this; }
        public Builder updatedAt(Instant v)            { this.updatedAt = v; return this; }
        public Builder weeklyDueDays(Set<DayOfWeek> v) { this.weeklyDueDays = v; return this; }
        public Builder monthlyDueDay(Integer v)        { this.monthlyDueDay = v; return this; }
        public Challenge build()                       { return new Challenge(this); }
    }
}
