package com.monthlychallenge.application.ports.in;

import com.monthlychallenge.application.dto.FriendFeedEntry;

import java.util.List;
import java.util.UUID;

public interface FriendsFeedUseCase {
    List<FriendFeedEntry> getFriendsFeed(UUID userId);
}
