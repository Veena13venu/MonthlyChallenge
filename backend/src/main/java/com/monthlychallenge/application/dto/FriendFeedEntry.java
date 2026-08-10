package com.monthlychallenge.application.dto;

import com.monthlychallenge.domain.model.Challenge;
import com.monthlychallenge.domain.model.CheckIn;

import java.util.List;
import java.util.UUID;

public record FriendFeedEntry(
        UUID friendUserId,
        String username,
        String displayName,
        String profilePhotoUrl,
        int currentStreak,
        int totalSharedChallenges,
        int completedToday,
        int halfCompletedToday,
        List<ChallengeWithCheckIn> sharedChallenges
) {
    public record ChallengeWithCheckIn(Challenge challenge, CheckIn todaysCheckIn) {}
}
