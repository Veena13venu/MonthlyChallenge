package com.monthlychallenge.infrastructure.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Simple notification logger — logs notifications instead of sending push messages.
 * You can upgrade this later to use email, Firebase, or any other service.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void sendFriendRequestNotification(UUID addresseeId, String requesterUsername) {
        log.info("Friend request from {} to user {}", requesterUsername, addresseeId);
    }

    public void sendFriendRequestAcceptedNotification(UUID requesterId, String acceptorUsername) {
        log.info("Friend request accepted by {} for user {}", acceptorUsername, requesterId);
    }
}
