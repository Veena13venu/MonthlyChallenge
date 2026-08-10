package com.monthlychallenge.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public final class CheckIn {

    private final UUID id;
    private final UUID challengeId;
    private final UUID userId;
    private final LocalDate date;
    private final CheckInStatus status;
    private final Double actualValue;
    private final Instant createdAt;
    private final Instant updatedAt;

    private CheckIn(Builder b) {
        this.id = b.id; this.challengeId = b.challengeId; this.userId = b.userId;
        this.date = b.date; this.status = b.status; this.actualValue = b.actualValue;
        this.createdAt = b.createdAt; this.updatedAt = b.updatedAt;
    }

    public UUID getId()             { return id; }
    public UUID getChallengeId()    { return challengeId; }
    public UUID getUserId()         { return userId; }
    public LocalDate getDate()      { return date; }
    public CheckInStatus getStatus(){ return status; }
    public Double getActualValue()  { return actualValue; }
    public Instant getCreatedAt()   { return createdAt; }
    public Instant getUpdatedAt()   { return updatedAt; }

    public double pointValue() {
        return switch (status) {
            case COMPLETED      -> 1.0;
            case HALF_COMPLETED -> 0.5;
            case MISSED         -> 0.0;
        };
    }

    public CheckIn withStatus(CheckInStatus v)   { return toBuilder().status(v).build(); }
    public CheckIn withActualValue(Double v)     { return toBuilder().actualValue(v).build(); }
    public CheckIn withUpdatedAt(Instant v)      { return toBuilder().updatedAt(v).build(); }

    public static Builder builder() { return new Builder(); }
    private Builder toBuilder() {
        return new Builder().id(id).challengeId(challengeId).userId(userId).date(date)
                .status(status).actualValue(actualValue).createdAt(createdAt).updatedAt(updatedAt);
    }

    public static final class Builder {
        private UUID id; private UUID challengeId; private UUID userId;
        private LocalDate date; private CheckInStatus status; private Double actualValue;
        private Instant createdAt; private Instant updatedAt;

        public Builder id(UUID v)              { this.id = v; return this; }
        public Builder challengeId(UUID v)     { this.challengeId = v; return this; }
        public Builder userId(UUID v)          { this.userId = v; return this; }
        public Builder date(LocalDate v)       { this.date = v; return this; }
        public Builder status(CheckInStatus v) { this.status = v; return this; }
        public Builder actualValue(Double v)   { this.actualValue = v; return this; }
        public Builder createdAt(Instant v)    { this.createdAt = v; return this; }
        public Builder updatedAt(Instant v)    { this.updatedAt = v; return this; }
        public CheckIn build()                 { return new CheckIn(this); }
    }
}
