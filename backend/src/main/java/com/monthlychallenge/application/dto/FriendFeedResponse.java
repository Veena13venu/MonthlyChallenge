package com.monthlychallenge.application.dto;

import com.monthlychallenge.domain.enums.CheckInStatus;

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
