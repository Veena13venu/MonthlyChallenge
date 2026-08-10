package com.monthlychallenge.application.port.out;

import com.monthlychallenge.domain.model.Friendship;
import com.monthlychallenge.domain.model.FriendshipStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port — persistence contract for {@link Friendship}.
 */
public interface FriendshipRepository {

    Friendship save(Friendship friendship);

    Optional<Friendship> findById(UUID id);

    /** Checks whether a pending or accepted friendship already exists between two users. */
    Optional<Friendship> findBetween(UUID userA, UUID userB);

    /** Finds any row between two users regardless of status (used to reuse DECLINED rows). */
    Optional<Friendship> findBetweenAnyStatus(UUID userA, UUID userB);

    List<Friendship> findByUserIdAndStatus(UUID userId, FriendshipStatus status);

    /** Returns all accepted friends regardless of who initiated the request. */
    List<Friendship> findAcceptedFriends(UUID userId);
}
