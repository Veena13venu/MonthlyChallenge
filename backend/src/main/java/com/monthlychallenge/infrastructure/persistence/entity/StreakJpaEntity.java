package com.monthlychallenge.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "streaks",
       uniqueConstraints = @UniqueConstraint(name = "uq_streak_user", columnNames = "user_id"))
public class StreakJpaEntity {

    @Id @Column(columnDefinition = "uuid")                                 private UUID id;
    @Column(name = "user_id", nullable = false, columnDefinition = "uuid") private UUID userId;
    @Column(name = "current_streak", nullable = false)                     private int currentStreak;
    @Column(name = "longest_streak", nullable = false)                     private int longestStreak;
    @Column(name = "last_success_date")                                    private LocalDate lastSuccessDate;

    public StreakJpaEntity() {}

    public UUID getId()                    { return id; }
    public void setId(UUID v)              { this.id = v; }
    public UUID getUserId()                { return userId; }
    public void setUserId(UUID v)          { this.userId = v; }
    public int getCurrentStreak()          { return currentStreak; }
    public void setCurrentStreak(int v)    { this.currentStreak = v; }
    public int getLongestStreak()          { return longestStreak; }
    public void setLongestStreak(int v)    { this.longestStreak = v; }
    public LocalDate getLastSuccessDate()  { return lastSuccessDate; }
    public void setLastSuccessDate(LocalDate v) { this.lastSuccessDate = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final StreakJpaEntity e = new StreakJpaEntity();
        public Builder id(UUID v)                  { e.id = v; return this; }
        public Builder userId(UUID v)              { e.userId = v; return this; }
        public Builder currentStreak(int v)        { e.currentStreak = v; return this; }
        public Builder longestStreak(int v)        { e.longestStreak = v; return this; }
        public Builder lastSuccessDate(LocalDate v){ e.lastSuccessDate = v; return this; }
        public StreakJpaEntity build()             { return e; }
    }
}
