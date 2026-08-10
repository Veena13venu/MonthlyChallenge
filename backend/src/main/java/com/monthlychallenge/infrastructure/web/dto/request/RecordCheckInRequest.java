package com.monthlychallenge.infrastructure.web.dto.request;

import com.monthlychallenge.domain.model.CheckInStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class RecordCheckInRequest {

    @NotNull(message = "challengeId is required") private UUID challengeId;
    @NotNull(message = "status is required")      private CheckInStatus status;
    private Double actualValue;

    public UUID getChallengeId()          { return challengeId; }
    public void setChallengeId(UUID v)    { this.challengeId = v; }
    public CheckInStatus getStatus()      { return status; }
    public void setStatus(CheckInStatus v){ this.status = v; }
    public Double getActualValue()        { return actualValue; }
    public void setActualValue(Double v)  { this.actualValue = v; }
}
