package com.monthlychallenge.domain.models;

import com.monthlychallenge.domain.enums.FriendshipStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Friendship {
    private UUID id;
    private UUID requesterId;
    private UUID addresseeId;
    private FriendshipStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean involves(UUID userId) {
        return requesterId.equals(userId) || addresseeId.equals(userId);
    }
}
