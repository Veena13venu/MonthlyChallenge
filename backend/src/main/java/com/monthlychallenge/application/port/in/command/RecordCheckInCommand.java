package com.monthlychallenge.application.port.in.command;

import com.monthlychallenge.domain.model.CheckInStatus;
import java.util.UUID;

public record RecordCheckInCommand(UUID challengeId, CheckInStatus status, Double actualValue) {}
