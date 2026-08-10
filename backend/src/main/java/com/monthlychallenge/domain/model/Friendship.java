package com.monthlychallenge.domain.model;

import java.time.Instant;
import java.util.UUID;

public final class Friendship {

    private final UUID id;
    private final UUID requesterId;
    private final UUID addresseeId;
    private final FriendshipStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Friendship(Builder b) {
        this.id = b.id; this.requesterId = b.requesterId; this.addresseeId = b.addresseeId;
        this.status = b.status; this.createdAt = b.createdAt; this.updatedAt = b.updatedAt;
    }

    public UUID getId()               { return id; }
    public UUID getRequesterId()      { return requesterId; }
    public UUID getAddresseeId()      { return addresseeId; }
    public FriendshipStatus getStatus(){ return status; }
    public Instant getCreatedAt()     { return createdAt; }
    public Instant getUpdatedAt()     { return updatedAt; }

    public boolean involves(UUID otherUserId) {
        return requesterId.equals(otherUserId) || addresseeId.equals(otherUserId);
    }
    public UUID friendOf(UUID userId) {
        return requesterId.equals(userId) ? addresseeId : requesterId;
    }

    public Friendship withStatus(FriendshipStatus v) { return toBuilder().status(v).build(); }
    public Friendship withUpdatedAt(Instant v)       { return toBuilder().updatedAt(v).build(); }

    public static Builder builder() { return new Builder(); }
    private Builder toBuilder() {
        return new Builder().id(id).requesterId(requesterId).addresseeId(addresseeId)
                .status(status).createdAt(createdAt).updatedAt(updatedAt);
    }

    public static final class Builder {
        private UUID id; private UUID requesterId; private UUID addresseeId;
        private FriendshipStatus status; private Instant createdAt; private Instant updatedAt;

        public Builder id(UUID v)                  { this.id = v; return this; }
        public Builder requesterId(UUID v)         { this.requesterId = v; return this; }
        public Builder addresseeId(UUID v)         { this.addresseeId = v; return this; }
        public Builder status(FriendshipStatus v)  { this.status = v; return this; }
        public Builder createdAt(Instant v)        { this.createdAt = v; return this; }
        public Builder updatedAt(Instant v)        { this.updatedAt = v; return this; }
        public Friendship build()                  { return new Friendship(this); }
    }
}
