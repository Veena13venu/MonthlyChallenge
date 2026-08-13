package com.monthlychallenge.application.ports.out;

import com.monthlychallenge.domain.models.DaySummary;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DaySummaryRepository {
    DaySummary save(DaySummary daySummary);
    Optional<DaySummary> findByUserIdAndDate(UUID userId, LocalDate date);
    List<DaySummary> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to);
}
