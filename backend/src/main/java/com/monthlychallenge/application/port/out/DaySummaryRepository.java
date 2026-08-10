package com.monthlychallenge.application.port.out;

import com.monthlychallenge.domain.model.DaySummary;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port — persistence contract for {@link DaySummary}.
 */
public interface DaySummaryRepository {

    DaySummary save(DaySummary daySummary);

    Optional<DaySummary> findByUserIdAndDate(UUID userId, LocalDate date);

    List<DaySummary> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to);
}
