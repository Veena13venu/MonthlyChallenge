package com.monthlychallenge.application.service;

import com.monthlychallenge.application.port.in.FriendshipUseCase;
import com.monthlychallenge.application.port.out.FriendshipRepository;
import com.monthlychallenge.application.port.out.NotificationPort;
import com.monthlychallenge.application.port.out.UserRepository;
import com.monthlychallenge.domain.model.Friendship;
import com.monthlychallenge.domain.model.FriendshipStatus;
import com.monthlychallenge.infrastructure.exception.BusinessException;
import com.monthlychallenge.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FriendshipService implements FriendshipUseCase {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final NotificationPort notificationPort;

    public FriendshipService(FriendshipRepository friendshipRepository,
                              UserRepository userRepository,
                              NotificationPort notificationPort) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    public Friendship sendFriendRequest(UUID requesterId, UUID addresseeId) {
        if (requesterId.equals(addresseeId))
            throw new BusinessException("Cannot send a friend request to yourself");
        // Block if an active (PENDING or ACCEPTED) friendship already exists
        friendshipRepository.findBetween(requesterId, addresseeId).ifPresent(f -> {
            throw new BusinessException("A friend request or friendship already exists");
        });
        var requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Requester not found"));
        // Reuse any existing DECLINED row (reset to PENDING) to avoid duplicate DB rows
        Friendship friendship = friendshipRepository.findBetweenAnyStatus(requesterId, addresseeId)
                .map(existing -> existing
                        .withStatus(FriendshipStatus.PENDING)
                        .withUpdatedAt(Instant.now()))
                .orElseGet(() -> Friendship.builder()
                        .id(UUID.randomUUID()).requesterId(requesterId).addresseeId(addresseeId)
                        .status(FriendshipStatus.PENDING).createdAt(Instant.now()).updatedAt(Instant.now())
                        .build());
        Friendship saved = friendshipRepository.save(friendship);
        notificationPort.sendFriendRequestNotification(addresseeId, requester.getUsername());
        return saved;
    }

    @Override
    public Friendship acceptFriendRequest(UUID addresseeId, UUID friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));
        if (!friendship.getAddresseeId().equals(addresseeId))
            throw new BusinessException("Not authorised to accept this request");
        if (friendship.getStatus() != FriendshipStatus.PENDING)
            throw new BusinessException("Request is not in PENDING state");
        var acceptor = userRepository.findById(addresseeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Friendship saved = friendshipRepository.save(
                friendship.withStatus(FriendshipStatus.ACCEPTED).withUpdatedAt(Instant.now()));
        notificationPort.sendFriendRequestAcceptedNotification(friendship.getRequesterId(), acceptor.getUsername());
        return saved;
    }

    @Override
    public void declineFriendRequest(UUID addresseeId, UUID friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));
        if (!friendship.getAddresseeId().equals(addresseeId))
            throw new BusinessException("Not authorised to decline this request");
        friendshipRepository.save(friendship.withStatus(FriendshipStatus.DECLINED).withUpdatedAt(Instant.now()));
    }

    @Override
    public void removeFriend(UUID userId, UUID friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Friendship not found"));
        if (!friendship.involves(userId))
            throw new BusinessException("Not part of this friendship");
        friendshipRepository.save(friendship.withStatus(FriendshipStatus.DECLINED).withUpdatedAt(Instant.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Friendship> getAcceptedFriends(UUID userId) {
        return friendshipRepository.findAcceptedFriends(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Friendship> getPendingRequests(UUID userId) {
        return friendshipRepository.findByUserIdAndStatus(userId, FriendshipStatus.PENDING);
    }
}
