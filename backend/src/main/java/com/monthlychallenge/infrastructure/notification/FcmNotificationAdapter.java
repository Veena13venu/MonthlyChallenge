package com.monthlychallenge.infrastructure.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.monthlychallenge.application.port.out.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FcmNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(FcmNotificationAdapter.class);
    private final Map<UUID, String> deviceTokens = new ConcurrentHashMap<>();

    @Override
    public void sendChallengeReminder(UUID userId, String challengeTitle) {
        String token = deviceTokens.get(userId);
        if (token == null) { log.debug("No device token for user {}", userId); return; }
        sendPush(token, "Challenge Reminder", "Time to work on: " + challengeTitle);
    }

    @Override
    public void sendEndOfDaySummary(UUID userId, double pointsScored, double threshold) {
        String token = deviceTokens.get(userId);
        if (token == null) return;
        sendPush(token, "Today's Summary",
                String.format("You scored %.1f / %.1f today. Don't miss your goal!", pointsScored, threshold));
    }

    @Override
    public void sendFriendRequestNotification(UUID addresseeId, String requesterUsername) {
        String token = deviceTokens.get(addresseeId);
        if (token == null) return;
        sendPush(token, "New Friend Request", requesterUsername + " wants to be your accountability friend.");
    }

    @Override
    public void sendFriendRequestAcceptedNotification(UUID requesterId, String acceptorUsername) {
        String token = deviceTokens.get(requesterId);
        if (token == null) return;
        sendPush(token, "Friend Request Accepted", acceptorUsername + " accepted your friend request!");
    }

    @Override
    public void sendStreakMilestone(UUID userId, int streak) {
        String token = deviceTokens.get(userId);
        if (token == null) return;
        sendPush(token, "Streak Milestone! 🔥", "Amazing! You're on a " + streak + "-day streak. Keep it up!");
    }

    @Override
    public void registerDeviceToken(UUID userId, String fcmToken) {
        deviceTokens.put(userId, fcmToken);
        log.info("Registered FCM token for user {}", userId);
    }

    private void sendPush(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            log.debug("FCM message sent: {}", response);
        } catch (Exception e) {
            log.error("Failed to send FCM message: {}", e.getMessage());
        }
    }
}
