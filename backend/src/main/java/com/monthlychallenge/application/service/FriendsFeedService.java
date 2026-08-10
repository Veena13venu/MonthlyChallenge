package com.monthlychallenge.application.service;

import com.monthlychallenge.application.dto.FriendFeedEntry;
import com.monthlychallenge.application.port.in.FriendsFeedUseCase;
import com.monthlychallenge.application.port.out.*;
import com.monthlychallenge.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
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

        return friendshipRepository.findAcceptedFriends(userId).stream().map(f -> {
            UUID friendId = f.friendOf(userId);
            User friend = userRepository.findById(friendId).orElseThrow();

            List<Challenge> shared = challengeRepository
                    .findActiveByOwnerIdAndMonth(friendId, currentMonth).stream()
                    .filter(c -> c.getVisibility() == ChallengeVisibility.SHARED)
                    .filter(c -> c.isDueOnDay(today.getDayOfMonth(), today.getDayOfWeek()))
                    .toList();

            List<CheckIn> todayCheckIns = checkInRepository.findByUserIdAndDate(friendId, today);
            Map<UUID, CheckIn> byChallenge = todayCheckIns.stream()
                    .collect(Collectors.toMap(CheckIn::getChallengeId, ci -> ci));

            int completed    = (int) todayCheckIns.stream().filter(ci -> ci.getStatus() == CheckInStatus.COMPLETED).count();
            int halfComplete = (int) todayCheckIns.stream().filter(ci -> ci.getStatus() == CheckInStatus.HALF_COMPLETED).count();
            int currentStreak = streakRepository.findByUserId(friendId)
                    .map(Streak::getCurrentStreak).orElse(0);

            List<FriendFeedEntry.ChallengeWithCheckIn> details = shared.stream()
                    .map(c -> new FriendFeedEntry.ChallengeWithCheckIn(c, byChallenge.get(c.getId())))
                    .toList();

            return new FriendFeedEntry(friendId, friend.getUsername(), friend.getDisplayName(),
                    friend.getProfilePhotoUrl(), currentStreak, shared.size(),
                    completed, halfComplete, details);
        }).toList();
    }
}
