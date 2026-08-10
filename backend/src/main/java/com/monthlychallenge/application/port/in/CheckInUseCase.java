package com.monthlychallenge.application.port.in;

import com.monthlychallenge.application.port.in.command.RecordCheckInCommand;
import com.monthlychallenge.domain.model.CheckIn;
import com.monthlychallenge.domain.model.DaySummary;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Inbound port — daily check-in use cases (FR-11 to FR-15, Section 4.3).
 */
public interface CheckInUseCase {

    /**
     * Records or updates a check-in for a specific challenge on today's date.
     * Update is allowed until local midnight (FR-13).
     */
    CheckIn recordCheckIn(UUID userId, RecordCheckInCommand command);

    /** Returns all check-ins for the user on the given date. */
    List<CheckIn> getCheckInsForDate(UUID userId, LocalDate date);

    /**
     * Computes and returns the live day summary for today — used by the
     * home screen progress bar (Section 4.3, step 4).
     */
    DaySummary getLiveDaySummary(UUID userId, LocalDate date);
}
