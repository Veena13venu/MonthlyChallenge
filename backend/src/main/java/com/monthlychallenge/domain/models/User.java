package com.monthlychallenge.domain.models;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class User {
    private UUID id;
    private String keycloakId;
    private String username;
    private String displayName;
    private String email;
    private String profilePhotoUrl;
    private MinimumDailyTarget minimumDailyTarget;
    private Instant createdAt;
    private Instant updatedAt;
}
