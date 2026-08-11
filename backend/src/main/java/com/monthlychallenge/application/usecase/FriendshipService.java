package com.monthlychallenge.application.usecase;

import com.monthlychallenge.adapter.out.persistence.friendship.FriendshipJpaEntity;
import com.monthlychallenge.adapter.out.persistence.friendship.FriendshipJpaRepository;
import com.monthlychallenge.adapter.out.persistence.user.UserJpaRepository;
import com.monthlychallenge.domain.enums.FriendshipStatus;
import com.monthlychallenge.domain.exceptions.BusinessException;
import com.monthlychallenge.domain.exceptions.ResourceNotFoundException;
import com.monthlychallenge.infrastructure.notification.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FriendshipService {

    private final FriendshipJpaRepository friendshipRepo;
    private final UserJpaRepository userRepo;
    private final NotificationService notificationService;

    public FriendshipService(FriendshipJpaRepository friendshipRepo,
                              UserJpaRepository userRepo,
                              NotificationService notificationService) {
        this.friendshipRepo = friendshipRepo;
        this.userRepo = userRepo;
        this.notificationService = notificationService;
    }

    public FriendshipJpaEntity sendFriendRequest(UUID requesterId, UUID addresseeId) {
        if (requesterId.equals(addresseeId))
            throw new BusinessException("Cannot send friend request to yourself");

        userRepo.findById(addresseeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        friendshipRepo.findBetween(requesterId, addresseeId).ifPresent(f -> {
            throw new BusinessException("A friend request or friendship already exists");
        });

        var requester = userRepo.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Requester not found"));

        FriendshipJpaEntity f = friendshipRepo.findBetweenAnyStatus(requesterId, addresseeId)
                .orElseGet(() -> {
                    FriendshipJpaEntity entity = new FriendshipJpaEntity();
                    entity.setId(UUID.randomUUID());
                    entity.setRequesterId(requesterId);
                    entity.setAddresseeId(addresseeId);
                    entity.setCreatedAt(Instant.now());
                    return entity;
                });

        f.setStatus(FriendshipStatus.PENDING.name());
        f.setUpdatedAt(Instant.now());
        FriendshipJpaEntity saved = friendshipRepo.save(f);

        notificationService.sendFriendRequestNotification(addresseeId, requester.getUsername());
        return saved;
    }

    public FriendshipJpaEntity acceptFriendRequest(UUID addresseeId, UUID friendshipId) {
        FriendshipJpaEntity f = friendshipRepo.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));

        if (!f.getAddresseeId().equals(addresseeId))
            throw new BusinessException("Not authorised to accept this request");

        if (!FriendshipStatus.PENDING.name().equals(f.getStatus()))
            throw new BusinessException("Request is not in PENDING state");

        var acceptor = userRepo.findById(addresseeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        f.setStatus(FriendshipStatus.ACCEPTED.name());
        f.setUpdatedAt(Instant.now());
        FriendshipJpaEntity saved = friendshipRepo.save(f);

        notificationService.sendFriendRequestAcceptedNotification(f.getRequesterId(), acceptor.getUsername());
        return saved;
    }

    public void declineFriendRequest(UUID addresseeId, UUID friendshipId) {
        FriendshipJpaEntity f = friendshipRepo.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));

        if (!f.getAddresseeId().equals(addresseeId))
            throw new BusinessException("Not authorised to decline this request");

        f.setStatus(FriendshipStatus.DECLINED.name());
        f.setUpdatedAt(Instant.now());
        friendshipRepo.save(f);
    }

    public void removeFriend(UUID userId, UUID friendshipId) {
        FriendshipJpaEntity f = friendshipRepo.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Friendship not found"));

        if (!f.getRequesterId().equals(userId) && !f.getAddresseeId().equals(userId))
            throw new BusinessException("Not part of this friendship");

        f.setStatus(FriendshipStatus.DECLINED.name());
        f.setUpdatedAt(Instant.now());
        friendshipRepo.save(f);
    }

    @Transactional(readOnly = true)
    public List<FriendshipJpaEntity> getAcceptedFriends(UUID userId) {
        return friendshipRepo.findAcceptedFriends(userId);
    }

    @Transactional(readOnly = true)
    public List<FriendshipJpaEntity> getPendingRequests(UUID userId) {
        return friendshipRepo.findByUserIdAndStatus(userId, FriendshipStatus.PENDING.name());
    }
}
