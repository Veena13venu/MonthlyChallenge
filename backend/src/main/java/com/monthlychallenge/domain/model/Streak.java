package com.monthlychallenge.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public final class Streak {

    private final UUID id;
    private final UUID userId;
    private final int currentStreak;
    private final int longestStreak;
    private final LocalDate lastSuccessDate;

    private Streak(Builder b) {
        this.id = b.id; this.userId = b.userId; this.currentStreak = b.currentStreak;
        this.longestStreak = b.longestStreak; this.lastSuccessDate = b.lastSuccessDate;
    }

    public UUID getId()                    { return id; }
    public UUID getUserId()                { return userId; }
    public int getCurrentStreak()          { return currentStreak; }
    public int getLongestStreak()          { return longestStreak; }
    public LocalDate getLastSuccessDate()  { return lastSuccessDate; }

    public Streak withCurrentStreak(int v)         { return toBuilder().currentStreak(v).build(); }
    public Streak withLongestStreak(int v)         { return toBuilder().longestStreak(v).build(); }
    public Streak withLastSuccessDate(LocalDate v) { return toBuilder().lastSuccessDate(v).build(); }

    public Streak applyDayResult(DayResult dayResult, LocalDate date) {
        if (dayResult == DayResult.SUCCESS) {
            int newCurrent = currentStreak + 1;
            int newLongest = Math.max(newCurrent, longestStreak);
            return toBuilder().currentStreak(newCurrent).longestStreak(newLongest)
                    .lastSuccessDate(date).build();
        }
        return toBuilder().currentStreak(0).build();
    }

    public static Builder builder() { return new Builder(); }
    private Builder toBuilder() {
        return new Builder().id(id).userId(userId).currentStreak(currentStreak)
                .longestStreak(longestStreak).lastSuccessDate(lastSuccessDate);
    }

    public static final class Builder {
        private UUID id; private UUID userId; private int currentStreak;
        private int longestStreak; private LocalDate lastSuccessDate;

        public Builder id(UUID v)                  { this.id = v; return this; }
        public Builder userId(UUID v)              { this.userId = v; return this; }
        public Builder currentStreak(int v)        { this.currentStreak = v; return this; }
        public Builder longestStreak(int v)        { this.longestStreak = v; return this; }
        public Builder lastSuccessDate(LocalDate v){ this.lastSuccessDate = v; return this; }
        public Streak build()                      { return new Streak(this); }
    }
}
