package com.monthlychallenge.adapter.in.rest.dto.request;

import com.monthlychallenge.domain.enums.ChallengeCategory;
import com.monthlychallenge.domain.enums.ChallengeFrequency;
import com.monthlychallenge.domain.enums.ChallengeVisibility;
import jakarta.validation.constraints.*;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.Set;

public class CreateChallengeRequest {

    @NotBlank(message = "Title is required") @Size(max = 150)
    private String title;
    @Size(max = 500) private String description;
    @NotNull(message = "Category is required") private ChallengeCategory category;
    @NotNull(message = "Frequency is required") private ChallengeFrequency frequency;
    @NotNull(message = "Month is required")     private YearMonth month;
    private ChallengeVisibility visibility;
    private Double targetValue;
    private String targetUnit;
    @Min(0) @Max(23) private Integer reminderHour;
    @Min(0) @Max(59) private Integer reminderMinute;
    private Set<DayOfWeek> weeklyDueDays;
    @Min(1) @Max(28) private Integer monthlyDueDay;

    public String getTitle()                       { return title; }
    public void setTitle(String v)                 { this.title = v; }
    public String getDescription()                 { return description; }
    public void setDescription(String v)           { this.description = v; }
    public ChallengeCategory getCategory()         { return category; }
    public void setCategory(ChallengeCategory v)   { this.category = v; }
    public ChallengeFrequency getFrequency()        { return frequency; }
    public void setFrequency(ChallengeFrequency v)  { this.frequency = v; }
    public YearMonth getMonth()                    { return month; }
    public void setMonth(YearMonth v)              { this.month = v; }
    public ChallengeVisibility getVisibility()     { return visibility; }
    public void setVisibility(ChallengeVisibility v){ this.visibility = v; }
    public Double getTargetValue()                 { return targetValue; }
    public void setTargetValue(Double v)           { this.targetValue = v; }
    public String getTargetUnit()                  { return targetUnit; }
    public void setTargetUnit(String v)            { this.targetUnit = v; }
    public Integer getReminderHour()               { return reminderHour; }
    public void setReminderHour(Integer v)         { this.reminderHour = v; }
    public Integer getReminderMinute()             { return reminderMinute; }
    public void setReminderMinute(Integer v)       { this.reminderMinute = v; }
    public Set<DayOfWeek> getWeeklyDueDays()       { return weeklyDueDays; }
    public void setWeeklyDueDays(Set<DayOfWeek> v) { this.weeklyDueDays = v; }
    public Integer getMonthlyDueDay()              { return monthlyDueDay; }
    public void setMonthlyDueDay(Integer v)        { this.monthlyDueDay = v; }
}
