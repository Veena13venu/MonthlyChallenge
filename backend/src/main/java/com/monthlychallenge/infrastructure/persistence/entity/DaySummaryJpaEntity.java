package com.monthlychallenge.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "day_summaries",
        uniqueConstraints = @UniqueConstraint(name = "uq_day_summary_user_date", columnNames = {"user_id","date"}))
public class DaySummaryJpaEntity {

    @Id @Column(columnDefinition = "uuid")                                  private UUID id;
    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")  private UUID userId;
    @Column(nullable = false)                                               private LocalDate date;
    @Column(name = "total_points",      nullable = false)                   private double totalPoints;
    @Column(name = "minimum_threshold", nullable = false)                   private double minimumThreshold;
    @Column(nullable = false, length = 10)                                  private String result;

    public DaySummaryJpaEntity() {}

    public UUID getId()                   { return id; }
    public void setId(UUID v)             { this.id = v; }
    public UUID getUserId()               { return userId; }
    public void setUserId(UUID v)         { this.userId = v; }
    public LocalDate getDate()            { return date; }
    public void setDate(LocalDate v)      { this.date = v; }
    public double getTotalPoints()        { return totalPoints; }
    public void setTotalPoints(double v)  { this.totalPoints = v; }
    public double getMinimumThreshold()   { return minimumThreshold; }
    public void setMinimumThreshold(double v){ this.minimumThreshold = v; }
    public String getResult()             { return result; }
    public void setResult(String v)       { this.result = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final DaySummaryJpaEntity e = new DaySummaryJpaEntity();
        public Builder id(UUID v)                { e.id = v; return this; }
        public Builder userId(UUID v)            { e.userId = v; return this; }
        public Builder date(LocalDate v)         { e.date = v; return this; }
        public Builder totalPoints(double v)     { e.totalPoints = v; return this; }
        public Builder minimumThreshold(double v){ e.minimumThreshold = v; return this; }
        public Builder result(String v)          { e.result = v; return this; }
        public DaySummaryJpaEntity build()       { return e; }
    }
}
