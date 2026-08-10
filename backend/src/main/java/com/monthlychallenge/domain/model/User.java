package com.monthlychallenge.domain.model;

import java.time.Instant;
import java.util.UUID;

public final class User {

    private final UUID id;
    private final String keycloakId;
    private final String username;
    private final String displayName;
    private final String email;
    private final String profilePhotoUrl;
    private final MinimumDailyTarget minimumDailyTarget;
    private final Instant createdAt;
    private final Instant updatedAt;

    private User(Builder b) {
        this.id = b.id; this.keycloakId = b.keycloakId; this.username = b.username;
        this.displayName = b.displayName; this.email = b.email;
        this.profilePhotoUrl = b.profilePhotoUrl; this.minimumDailyTarget = b.minimumDailyTarget;
        this.createdAt = b.createdAt; this.updatedAt = b.updatedAt;
    }

    public UUID getId() { return id; }
    public String getKeycloakId() { return keycloakId; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public MinimumDailyTarget getMinimumDailyTarget() { return minimumDailyTarget; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public User withDisplayName(String v)              { return toBuilder().displayName(v).build(); }
    public User withProfilePhotoUrl(String v)          { return toBuilder().profilePhotoUrl(v).build(); }
    public User withMinimumDailyTarget(MinimumDailyTarget v) { return toBuilder().minimumDailyTarget(v).build(); }
    public User withUpdatedAt(Instant v)               { return toBuilder().updatedAt(v).build(); }

    public static Builder builder() { return new Builder(); }
    private Builder toBuilder() {
        return new Builder().id(id).keycloakId(keycloakId).username(username)
                .displayName(displayName).email(email).profilePhotoUrl(profilePhotoUrl)
                .minimumDailyTarget(minimumDailyTarget).createdAt(createdAt).updatedAt(updatedAt);
    }

    public static final class Builder {
        private UUID id; private String keycloakId; private String username;
        private String displayName; private String email; private String profilePhotoUrl;
        private MinimumDailyTarget minimumDailyTarget; private Instant createdAt; private Instant updatedAt;

        public Builder id(UUID v)                          { this.id = v; return this; }
        public Builder keycloakId(String v)                { this.keycloakId = v; return this; }
        public Builder username(String v)                  { this.username = v; return this; }
        public Builder displayName(String v)               { this.displayName = v; return this; }
        public Builder email(String v)                     { this.email = v; return this; }
        public Builder profilePhotoUrl(String v)           { this.profilePhotoUrl = v; return this; }
        public Builder minimumDailyTarget(MinimumDailyTarget v) { this.minimumDailyTarget = v; return this; }
        public Builder createdAt(Instant v)                { this.createdAt = v; return this; }
        public Builder updatedAt(Instant v)                { this.updatedAt = v; return this; }
        public User build()                                { return new User(this); }
    }
}
