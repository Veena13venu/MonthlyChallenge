package com.monthlychallenge.application.ports.in;

import com.monthlychallenge.application.ports.in.command.RecordCheckInCommand;
import com.monthlychallenge.domain.models.CheckIn;
import com.monthlychallenge.domain.models.DaySummary;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CheckInUseCase {
    CheckIn recordCheckIn(UUID userId, RecordCheckInCommand command);
    List<CheckIn> getCheckInsForDate(UUID userId, LocalDate date);
    DaySummary getLiveDaySummary(UUID userId, LocalDate date);
}
