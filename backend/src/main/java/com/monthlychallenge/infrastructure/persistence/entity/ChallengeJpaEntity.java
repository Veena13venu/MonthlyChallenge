package com.monthlychallenge.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "challenges")
public class ChallengeJpaEntity {

    @Id @Column(columnDefinition = "uuid")                          private UUID id;
    @Column(name = "owner_id", nullable = false, columnDefinition = "uuid") private UUID ownerId;
    @Column(nullable = false, length = 150)                         private String title;
    @Column(length = 500)                                           private String description;
    @Column(nullable = false, length = 30)                          private String category;
    @Column(nullable = false, length = 20)                          private String frequency;
    @Column(name = "target_value", length = 50)                     private String targetValue;
    @Column(nullable = false, length = 7)                           private String month;
    @Column(nullable = false, length = 10)                          private String visibility;
    @Column(name = "reminder_hour")                                   private Integer reminderHour;
    @Column(name = "reminder_minute")                                 private Integer reminderMinute;
    @Column(name = "weekly_due_days", length = 100)                   private String weeklyDueDays;
    @Column(name = "monthly_due_day")                                 private Integer monthlyDueDay;
    @Column(nullable = false)                                       private boolean active;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false)                  private Instant updatedAt;

    public ChallengeJpaEntity() {}

    public UUID getId()                { return id; }
    public void setId(UUID v)          { this.id = v; }
    public UUID getOwnerId()           { return ownerId; }
    public void setOwnerId(UUID v)     { this.ownerId = v; }
    public String getTitle()           { return title; }
    public void setTitle(String v)     { this.title = v; }
    public String getDescription()     { return description; }
    public void setDescription(String v){ this.description = v; }
    public String getCategory()        { return category; }
    public void setCategory(String v)  { this.category = v; }
    public String getFrequency()       { return frequency; }
    public void setFrequency(String v) { this.frequency = v; }
    public String getTargetValue()     { return targetValue; }
    public void setTargetValue(String v){ this.targetValue = v; }
    public String getMonth()           { return month; }
    public void setMonth(String v)     { this.month = v; }
    public String getVisibility()      { return visibility; }
    public void setVisibility(String v){ this.visibility = v; }
    public Integer getReminderHour()   { return reminderHour; }
    public void setReminderHour(Integer v){ this.reminderHour = v; }
    public Integer getReminderMinute() { return reminderMinute; }
    public void setReminderMinute(Integer v){ this.reminderMinute = v; }
    public String getWeeklyDueDays()   { return weeklyDueDays; }
    public void setWeeklyDueDays(String v){ this.weeklyDueDays = v; }
    public Integer getMonthlyDueDay()  { return monthlyDueDay; }
    public void setMonthlyDueDay(Integer v){ this.monthlyDueDay = v; }
    public boolean isActive()          { return active; }
    public void setActive(boolean v)   { this.active = v; }
    public Instant getCreatedAt()      { return createdAt; }
    public void setCreatedAt(Instant v){ this.createdAt = v; }
    public Instant getUpdatedAt()      { return updatedAt; }
    public void setUpdatedAt(Instant v){ this.updatedAt = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final ChallengeJpaEntity e = new ChallengeJpaEntity();
        public Builder id(UUID v)               { e.id = v; return this; }
        public Builder ownerId(UUID v)          { e.ownerId = v; return this; }
        public Builder title(String v)          { e.title = v; return this; }
        public Builder description(String v)    { e.description = v; return this; }
        public Builder category(String v)       { e.category = v; return this; }
        public Builder frequency(String v)      { e.frequency = v; return this; }
        public Builder targetValue(String v)    { e.targetValue = v; return this; }
        public Builder month(String v)          { e.month = v; return this; }
        public Builder visibility(String v)     { e.visibility = v; return this; }
        public Builder reminderHour(Integer v)  { e.reminderHour = v; return this; }
        public Builder reminderMinute(Integer v){ e.reminderMinute = v; return this; }
        public Builder weeklyDueDays(String v)  { e.weeklyDueDays = v; return this; }
        public Builder monthlyDueDay(Integer v) { e.monthlyDueDay = v; return this; }
        public Builder active(boolean v)        { e.active = v; return this; }
        public Builder createdAt(Instant v)     { e.createdAt = v; return this; }
        public Builder updatedAt(Instant v)     { e.updatedAt = v; return this; }
        public ChallengeJpaEntity build()       { return e; }
    }
}
