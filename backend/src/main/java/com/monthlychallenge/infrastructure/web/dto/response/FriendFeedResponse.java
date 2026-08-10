package com.monthlychallenge.infrastructure.web.dto.response;

import com.monthlychallenge.domain.model.CheckInStatus;

import java.util.List;
import java.util.UUID;

public record FriendFeedResponse(
        UUID friendUserId,
        String username,
        String displayName,
        String profilePhotoUrl,
        int currentStreak,
        int totalSharedChallenges,
        int completedToday,
        int halfCompletedToday,
        List<FriendChallengeEntry> sharedChallenges
) {
    public record FriendChallengeEntry(UUID challengeId, String title, CheckInStatus status) {}
}
