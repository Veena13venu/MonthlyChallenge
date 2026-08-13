package com.monthlychallenge.infrastructure.notification;

import com.monthlychallenge.application.ports.out.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NotificationService implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Override
    public void sendChallengeReminder(UUID userId, String challengeTitle) {
        log.info("Reminder for user {}: {}", userId, challengeTitle);
    }

    @Override
    public void sendEndOfDaySummary(UUID userId, double pointsScored, double threshold) {
        log.info("End of day summary for user {}: scored {}/{}", userId, pointsScored, threshold);
    }

    @Override
    public void sendFriendRequestNotification(UUID addresseeId, String requesterUsername) {
        log.info("Friend request from {} to user {}", requesterUsername, addresseeId);
    }

    @Override
    public void sendFriendRequestAcceptedNotification(UUID requesterId, String acceptorUsername) {
        log.info("Friend request accepted by {} for user {}", acceptorUsername, requesterId);
    }

    @Override
    public void sendStreakMilestone(UUID userId, int streak) {
        log.info("Streak milestone for user {}: {}", userId, streak);
    }

    @Override
    public void registerDeviceToken(UUID userId, String fcmToken) {
        log.info("Registered device token for user {}", userId);
    }
}
