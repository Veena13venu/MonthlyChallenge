package com.monthlychallenge.application.ports.out;

import com.monthlychallenge.domain.enums.FriendshipStatus;
import com.monthlychallenge.domain.models.Friendship;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendshipRepository {
    Friendship save(Friendship friendship);
    Optional<Friendship> findById(UUID id);
    Optional<Friendship> findBetween(UUID userA, UUID userB);
    Optional<Friendship> findBetweenAnyStatus(UUID userA, UUID userB);
    List<Friendship> findByUserIdAndStatus(UUID userId, FriendshipStatus status);
    List<Friendship> findAcceptedFriends(UUID userId);
}
