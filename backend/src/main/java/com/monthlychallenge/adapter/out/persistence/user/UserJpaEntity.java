package com.monthlychallenge.adapter.out.persistence.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_users_keycloak_id", columnNames = "keycloak_id"),
                @UniqueConstraint(name = "uq_users_username",    columnNames = "username")
        })
@Getter @Setter @NoArgsConstructor
public class UserJpaEntity {

    @Id @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "keycloak_id", nullable = false, length = 255)
    private String keycloakId;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    // Stored as "value:isPercentage" in the DB (e.g. "1.0:false")
    @Column(name = "minimum_daily_target", nullable = false, length = 20)
    private String minimumDailyTarget;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Convenience helpers so services don't need to parse the string
    public double getMinimumTargetValue() {
        if (minimumDailyTarget == null) return 1.0;
        return Double.parseDouble(minimumDailyTarget.split(":")[0]);
    }

    public boolean isMinimumTargetIsPercentage() {
        if (minimumDailyTarget == null) return false;
        String[] p = minimumDailyTarget.split(":");
        return p.length > 1 && Boolean.parseBoolean(p[1]);
    }

    public void setMinimumTargetValue(double value) {
        minimumDailyTarget = value + ":" + isMinimumTargetIsPercentage();
    }

    public void setMinimumTargetIsPercentage(boolean pct) {
        minimumDailyTarget = getMinimumTargetValue() + ":" + pct;
    }
}
