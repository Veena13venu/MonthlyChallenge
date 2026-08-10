package com.monthlychallenge.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_users_keycloak_id", columnNames = "keycloak_id"),
                @UniqueConstraint(name = "uq_users_username",    columnNames = "username")
        })
public class UserJpaEntity {

    @Id @Column(columnDefinition = "uuid")
    private UUID id;
    @Column(name = "keycloak_id", nullable = false, length = 255) private String keycloakId;
    @Column(nullable = false, length = 50)                        private String username;
    @Column(name = "display_name", length = 100)                  private String displayName;
    @Column(nullable = false, length = 255)                       private String email;
    @Column(name = "profile_photo_url", length = 500)             private String profilePhotoUrl;
    @Column(name = "minimum_daily_target", nullable = false, length = 20) private String minimumDailyTarget;
    @Column(name = "created_at", nullable = false, updatable = false)    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)                       private Instant updatedAt;

    public UserJpaEntity() {}

    public UUID getId()                     { return id; }
    public void setId(UUID v)               { this.id = v; }
    public String getKeycloakId()           { return keycloakId; }
    public void setKeycloakId(String v)     { this.keycloakId = v; }
    public String getUsername()             { return username; }
    public void setUsername(String v)       { this.username = v; }
    public String getDisplayName()          { return displayName; }
    public void setDisplayName(String v)    { this.displayName = v; }
    public String getEmail()                { return email; }
    public void setEmail(String v)          { this.email = v; }
    public String getProfilePhotoUrl()      { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String v){ this.profilePhotoUrl = v; }
    public String getMinimumDailyTarget()   { return minimumDailyTarget; }
    public void setMinimumDailyTarget(String v) { this.minimumDailyTarget = v; }
    public Instant getCreatedAt()           { return createdAt; }
    public void setCreatedAt(Instant v)     { this.createdAt = v; }
    public Instant getUpdatedAt()           { return updatedAt; }
    public void setUpdatedAt(Instant v)     { this.updatedAt = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final UserJpaEntity e = new UserJpaEntity();
        public Builder id(UUID v)                  { e.id = v; return this; }
        public Builder keycloakId(String v)        { e.keycloakId = v; return this; }
        public Builder username(String v)          { e.username = v; return this; }
        public Builder displayName(String v)       { e.displayName = v; return this; }
        public Builder email(String v)             { e.email = v; return this; }
        public Builder profilePhotoUrl(String v)   { e.profilePhotoUrl = v; return this; }
        public Builder minimumDailyTarget(String v){ e.minimumDailyTarget = v; return this; }
        public Builder createdAt(Instant v)        { e.createdAt = v; return this; }
        public Builder updatedAt(Instant v)        { e.updatedAt = v; return this; }
        public UserJpaEntity build()               { return e; }
    }
}
