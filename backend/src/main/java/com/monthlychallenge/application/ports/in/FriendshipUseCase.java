package com.monthlychallenge.application.ports.in;

import com.monthlychallenge.domain.models.Friendship;

import java.util.List;
import java.util.UUID;

public interface FriendshipUseCase {
    Friendship sendFriendRequest(UUID requesterId, UUID addresseeId);
    Friendship acceptFriendRequest(UUID addresseeId, UUID friendshipId);
    void declineFriendRequest(UUID addresseeId, UUID friendshipId);
    void removeFriend(UUID userId, UUID friendshipId);
    List<Friendship> getAcceptedFriends(UUID userId);
    List<Friendship> getPendingRequests(UUID userId);
}
