package com.monthlychallenge.application.ports.out;

import java.util.UUID;

public interface NotificationPort {
    void sendChallengeReminder(UUID userId, String challengeTitle);
    void sendEndOfDaySummary(UUID userId, double pointsScored, double threshold);
    void sendFriendRequestNotification(UUID addresseeId, String requesterUsername);
    void sendFriendRequestAcceptedNotification(UUID requesterId, String acceptorUsername);
    void sendStreakMilestone(UUID userId, int streak);
    void registerDeviceToken(UUID userId, String fcmToken);
}
