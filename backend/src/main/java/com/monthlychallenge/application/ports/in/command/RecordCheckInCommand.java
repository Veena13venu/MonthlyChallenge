package com.monthlychallenge.application.ports.in.command;

import com.monthlychallenge.domain.enums.CheckInStatus;

import java.util.UUID;

public record RecordCheckInCommand(
        UUID challengeId,
        CheckInStatus status,
        Double actualValue
) {}
