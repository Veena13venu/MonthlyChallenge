package com.monthlychallenge.application.usecase;

import com.monthlychallenge.application.dto.FriendFeedEntry;
import com.monthlychallenge.application.ports.in.FriendsFeedUseCase;
import com.monthlychallenge.application.ports.out.*;
import com.monthlychallenge.domain.enums.ChallengeVisibility;
import com.monthlychallenge.domain.models.Challenge;
import com.monthlychallenge.domain.models.CheckIn;
import com.monthlychallenge.domain.models.Friendship;
import com.monthlychallenge.domain.models.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FriendsFeedService implements FriendsFeedUseCase {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final CheckInRepository checkInRepository;
    private final StreakRepository streakRepository;

    public FriendsFeedService(FriendshipRepository friendshipRepository,
                               UserRepository userRepository,
                               ChallengeRepository challengeRepository,
                               CheckInRepository checkInRepository,
                               StreakRepository streakRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.checkInRepository = checkInRepository;
        this.streakRepository = streakRepository;
    }

    @Override
    public List<FriendFeedEntry> getFriendsFeed(UUID userId) {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        List<Friendship> friends = friendshipRepository.findAcceptedFriends(userId);

        return friends.stream().map(f -> {
            UUID friendId = f.getRequesterId().equals(userId) ? f.getAddresseeId() : f.getRequesterId();
            User friend = userRepository.findById(friendId).orElse(null);
            if (friend == null) return null;

            int streak = streakRepository.findByUserId(friendId).map(s -> s.getCurrentStreak()).orElse(0);

            List<Challenge> shared = challengeRepository.findActiveByOwnerIdAndMonth(friendId, currentMonth)
                    .stream()
                    .filter(c -> c.getVisibility() != ChallengeVisibility.PRIVATE)
                    .toList();

            Map<UUID, CheckIn> checkInsToday = checkInRepository.findByUserIdAndDate(friendId, today)
                    .stream().collect(Collectors.toMap(CheckIn::getChallengeId, ci -> ci));

            int completedToday = 0;
            int halfCompletedToday = 0;

            List<FriendFeedEntry.ChallengeWithCheckIn> progressList = new java.util.ArrayList<>();
            for (Challenge c : shared) {
                CheckIn ci = checkInsToday.get(c.getId());
                if (ci != null) {
                    if (ci.getStatus() == com.monthlychallenge.domain.enums.CheckInStatus.COMPLETED) completedToday++;
                    if (ci.getStatus() == com.monthlychallenge.domain.enums.CheckInStatus.HALF_COMPLETED) halfCompletedToday++;
                }
                progressList.add(new FriendFeedEntry.ChallengeWithCheckIn(c, ci));
            }

            return new FriendFeedEntry(friendId, friend.getUsername(), friend.getDisplayName(),
                    friend.getProfilePhotoUrl(), streak, shared.size(), completedToday,
                    halfCompletedToday, progressList);

        }).filter(Objects::nonNull).toList();
    }
}
