package com.monthlychallenge.application.port.in;

import com.monthlychallenge.application.dto.FriendFeedEntry;

import java.util.List;
import java.util.UUID;

/**
 * Inbound port — friends' challenge visibility feed (FR-28 to FR-31, Section 4.7).
 */
public interface FriendsFeedUseCase {

    /**
     * Returns the friends tab feed: each accepted friend's current streak and
     * today's completion status (FR-31).
     * Only SHARED challenges count; PRIVATE challenges are excluded (FR-28, FR-29, FR-30).
     */
    List<FriendFeedEntry> getFriendsFeed(UUID userId);
}
