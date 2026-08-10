package com.monthlychallenge.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "friendships")
public class FriendshipJpaEntity {

    @Id @Column(columnDefinition = "uuid")                                       private UUID id;
    @Column(name = "requester_id", nullable = false, columnDefinition = "uuid")  private UUID requesterId;
    @Column(name = "addressee_id", nullable = false, columnDefinition = "uuid")  private UUID addresseeId;
    @Column(nullable = false, length = 20)                                       private String status;
    @Column(name = "created_at", nullable = false, updatable = false)            private Instant createdAt;
    @Column(name = "updated_at", nullable = false)                               private Instant updatedAt;

    public FriendshipJpaEntity() {}

    public UUID getId()               { return id; }
    public void setId(UUID v)         { this.id = v; }
    public UUID getRequesterId()      { return requesterId; }
    public void setRequesterId(UUID v){ this.requesterId = v; }
    public UUID getAddresseeId()      { return addresseeId; }
    public void setAddresseeId(UUID v){ this.addresseeId = v; }
    public String getStatus()         { return status; }
    public void setStatus(String v)   { this.status = v; }
    public Instant getCreatedAt()     { return createdAt; }
    public void setCreatedAt(Instant v){ this.createdAt = v; }
    public Instant getUpdatedAt()     { return updatedAt; }
    public void setUpdatedAt(Instant v){ this.updatedAt = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final FriendshipJpaEntity e = new FriendshipJpaEntity();
        public Builder id(UUID v)            { e.id = v; return this; }
        public Builder requesterId(UUID v)   { e.requesterId = v; return this; }
        public Builder addresseeId(UUID v)   { e.addresseeId = v; return this; }
        public Builder status(String v)      { e.status = v; return this; }
        public Builder createdAt(Instant v)  { e.createdAt = v; return this; }
        public Builder updatedAt(Instant v)  { e.updatedAt = v; return this; }
        public FriendshipJpaEntity build()   { return e; }
    }
}
