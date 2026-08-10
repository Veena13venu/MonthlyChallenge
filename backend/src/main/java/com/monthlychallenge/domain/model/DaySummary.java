package com.monthlychallenge.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public final class DaySummary {

    private final UUID id;
    private final UUID userId;
    private final LocalDate date;
    private final double totalPoints;
    private final double minimumThreshold;
    private final DayResult result;

    private DaySummary(Builder b) {
        this.id = b.id; this.userId = b.userId; this.date = b.date;
        this.totalPoints = b.totalPoints; this.minimumThreshold = b.minimumThreshold;
        this.result = b.result;
    }

    public UUID getId()                  { return id; }
    public UUID getUserId()              { return userId; }
    public LocalDate getDate()           { return date; }
    public double getTotalPoints()       { return totalPoints; }
    public double getMinimumThreshold()  { return minimumThreshold; }
    public DayResult getResult()         { return result; }

    public static DayResult evaluate(double totalPoints, double minimumThreshold) {
        if (totalPoints == 0.0)            return DayResult.MISSED;
        if (totalPoints >= minimumThreshold) return DayResult.SUCCESS;
        return DayResult.PARTIAL;
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private UUID id; private UUID userId; private LocalDate date;
        private double totalPoints; private double minimumThreshold; private DayResult result;

        public Builder id(UUID v)                  { this.id = v; return this; }
        public Builder userId(UUID v)              { this.userId = v; return this; }
        public Builder date(LocalDate v)           { this.date = v; return this; }
        public Builder totalPoints(double v)       { this.totalPoints = v; return this; }
        public Builder minimumThreshold(double v)  { this.minimumThreshold = v; return this; }
        public Builder result(DayResult v)         { this.result = v; return this; }
        public DaySummary build()                  { return new DaySummary(this); }
    }
}
