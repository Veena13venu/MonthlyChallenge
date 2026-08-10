package com.monthlychallenge.infrastructure.web.dto.response;

import java.time.LocalDate;

public record StreakResponse(int currentStreak, int longestStreak, LocalDate lastSuccessDate) {}
