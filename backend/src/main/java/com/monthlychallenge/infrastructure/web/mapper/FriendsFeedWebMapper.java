package com.monthlychallenge.infrastructure.web.mapper;

import com.monthlychallenge.application.dto.FriendFeedEntry;
import com.monthlychallenge.infrastructure.web.dto.response.FriendFeedResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FriendsFeedWebMapper {

    public FriendFeedResponse toResponse(FriendFeedEntry entry) {
        List<FriendFeedResponse.FriendChallengeEntry> challenges = entry.sharedChallenges()
                .stream()
                .map(c -> new FriendFeedResponse.FriendChallengeEntry(
                        c.challenge().getId(),
                        c.challenge().getTitle(),
                        c.todaysCheckIn() != null ? c.todaysCheckIn().getStatus() : null))
                .toList();

        return new FriendFeedResponse(
                entry.friendUserId(), entry.username(), entry.displayName(),
                entry.profilePhotoUrl(), entry.currentStreak(),
                entry.totalSharedChallenges(), entry.completedToday(),
                entry.halfCompletedToday(), challenges);
    }
}
