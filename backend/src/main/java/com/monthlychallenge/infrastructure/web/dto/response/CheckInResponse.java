package com.monthlychallenge.infrastructure.web.dto.response;

import com.monthlychallenge.domain.model.CheckInStatus;

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
