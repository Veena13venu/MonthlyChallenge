package com.monthlychallenge.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "check_ins",
        uniqueConstraints = @UniqueConstraint(name = "uq_checkin_user_challenge_date",
                columnNames = {"user_id", "challenge_id", "date"}))
public class CheckInJpaEntity {

    @Id @Column(columnDefinition = "uuid")                                private UUID id;
    @Column(name = "challenge_id", nullable = false, columnDefinition = "uuid") private UUID challengeId;
    @Column(name = "user_id",      nullable = false, columnDefinition = "uuid") private UUID userId;
    @Column(nullable = false)                                             private LocalDate date;
    @Column(nullable = false, length = 20)                                private String status;
    @Column(name = "actual_value")                                        private Double actualValue;
    @Column(name = "created_at", nullable = false, updatable = false)    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)                       private Instant updatedAt;

    public CheckInJpaEntity() {}

    public UUID getId()               { return id; }
    public void setId(UUID v)         { this.id = v; }
    public UUID getChallengeId()      { return challengeId; }
    public void setChallengeId(UUID v){ this.challengeId = v; }
    public UUID getUserId()           { return userId; }
    public void setUserId(UUID v)     { this.userId = v; }
    public LocalDate getDate()        { return date; }
    public void setDate(LocalDate v)  { this.date = v; }
    public String getStatus()         { return status; }
    public void setStatus(String v)   { this.status = v; }
    public Double getActualValue()    { return actualValue; }
    public void setActualValue(Double v){ this.actualValue = v; }
    public Instant getCreatedAt()     { return createdAt; }
    public void setCreatedAt(Instant v){ this.createdAt = v; }
    public Instant getUpdatedAt()     { return updatedAt; }
    public void setUpdatedAt(Instant v){ this.updatedAt = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final CheckInJpaEntity e = new CheckInJpaEntity();
        public Builder id(UUID v)            { e.id = v; return this; }
        public Builder challengeId(UUID v)   { e.challengeId = v; return this; }
        public Builder userId(UUID v)        { e.userId = v; return this; }
        public Builder date(LocalDate v)     { e.date = v; return this; }
        public Builder status(String v)      { e.status = v; return this; }
        public Builder actualValue(Double v) { e.actualValue = v; return this; }
        public Builder createdAt(Instant v)  { e.createdAt = v; return this; }
        public Builder updatedAt(Instant v)  { e.updatedAt = v; return this; }
        public CheckInJpaEntity build()      { return e; }
    }
}
