package com.monthlychallenge.application.dto;

import com.monthlychallenge.domain.enums.FriendshipStatus;

import java.time.Instant;
import java.util.UUID;

public record FriendshipResponse(
        UUID id,
        UUID requesterId,
        UUID addresseeId,
        FriendshipStatus status,
        Instant createdAt
) {}
