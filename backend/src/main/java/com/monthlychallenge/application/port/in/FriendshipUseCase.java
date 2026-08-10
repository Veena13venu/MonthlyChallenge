package com.monthlychallenge.application.port.in;

import com.monthlychallenge.domain.model.Friendship;

import java.util.List;
import java.util.UUID;

/**
 * Inbound port — friend system use cases (FR-23 to FR-27, Section 4.6).
 */
public interface FriendshipUseCase {

    /** Sends a friend request from the requester to the addressee (FR-23). */
    Friendship sendFriendRequest(UUID requesterId, UUID addresseeId);

    /** Accepts a pending friend request (FR-24, FR-25). */
    Friendship acceptFriendRequest(UUID addresseeId, UUID friendshipId);

    /** Declines a pending friend request silently (FR-24, Section 4.6). */
    void declineFriendRequest(UUID addresseeId, UUID friendshipId);

    /** Removes an existing friendship (FR-26). */
    void removeFriend(UUID userId, UUID friendshipId);

    /** Returns all accepted friends for a user. */
    List<Friendship> getAcceptedFriends(UUID userId);

    /** Returns all pending sent and received requests (FR-27). */
    List<Friendship> getPendingRequests(UUID userId);
}
