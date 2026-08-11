package com.monthlychallenge.domain.models;

import com.monthlychallenge.domain.enums.DayResult;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Streak {
    private UUID id;
    private UUID userId;
    private int currentStreak;
    private int longestStreak;
    private LocalDate lastSuccessDate;
    private Instant updatedAt;

    public Streak applyDayResult(DayResult result, LocalDate date) {
        int newCurrent;
        LocalDate newLastSuccess = lastSuccessDate;

        if (result == DayResult.SUCCESS) {
            if (lastSuccessDate != null && lastSuccessDate.equals(date.minusDays(1))) {
                newCurrent = currentStreak + 1;
            } else if (lastSuccessDate != null && lastSuccessDate.equals(date)) {
                newCurrent = currentStreak;
            } else {
                newCurrent = 1;
            }
            newLastSuccess = date;
        } else {
            newCurrent = 0;
        }

        int newLongest = Math.max(longestStreak, newCurrent);

        return Streak.builder()
                .id(id)
                .userId(userId)
                .currentStreak(newCurrent)
                .longestStreak(newLongest)
                .lastSuccessDate(newLastSuccess)
                .updatedAt(Instant.now())
                .build();
    }
}
