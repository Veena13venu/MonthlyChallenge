package com.monthlychallenge.infrastructure.web.dto.response;

import com.monthlychallenge.domain.model.FriendshipStatus;

import java.time.Instant;
import java.util.UUID;

public record FriendshipResponse(
        UUID id,
        UUID requesterId,
        UUID addresseeId,
        FriendshipStatus status,
        Instant createdAt
) {}
