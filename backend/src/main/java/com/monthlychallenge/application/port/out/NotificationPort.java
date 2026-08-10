package com.monthlychallenge.application.port.out;

import com.monthlychallenge.domain.model.User;

import java.util.UUID;

/**
 * Outbound port — push notification delivery.
 * Implemented by the Firebase FCM adapter in infrastructure.
 */
public interface NotificationPort {

    /** Sends a daily challenge reminder to the user. */
    void sendChallengeReminder(UUID userId, String challengeTitle);

    /** Sends an end-of-day summary alert if the minimum target has not been met. */
    void sendEndOfDaySummary(UUID userId, double pointsScored, double threshold);

    /** Notifies a user that they received a friend request (FR-35). */
    void sendFriendRequestNotification(UUID addresseeId, String requesterUsername);

    /** Notifies a user that their friend request was accepted (FR-35). */
    void sendFriendRequestAcceptedNotification(UUID requesterId, String acceptorUsername);

    /** Sends a streak milestone notification (FR-22). */
    void sendStreakMilestone(UUID userId, int streak);

    /**
     * Registers or updates the device FCM token for push delivery.
     * Called when the user logs in on a new device.
     */
    void registerDeviceToken(UUID userId, String fcmToken);
}
