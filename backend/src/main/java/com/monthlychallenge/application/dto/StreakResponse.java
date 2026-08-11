package com.monthlychallenge.application.dto;

import java.time.LocalDate;

public record StreakResponse(int currentStreak, int longestStreak, LocalDate lastSuccessDate) {}
