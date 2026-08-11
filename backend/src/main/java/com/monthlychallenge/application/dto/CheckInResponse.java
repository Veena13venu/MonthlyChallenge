package com.monthlychallenge.application.dto;

import com.monthlychallenge.domain.enums.CheckInStatus;

import java.time.LocalDate;
import java.util.UUID;

public record CheckInResponse(
        UUID id,
        UUID challengeId,
        LocalDate date,
        CheckInStatus status,
        Double actualValue,
        double pointValue
) {}
