package com.monthlychallenge.application.usecase;

import com.monthlychallenge.adapter.out.persistence.challenge.ChallengeJpaEntity;
import com.monthlychallenge.adapter.out.persistence.challenge.ChallengeJpaRepository;
import com.monthlychallenge.adapter.out.persistence.checkin.CheckInJpaEntity;
import com.monthlychallenge.adapter.out.persistence.checkin.CheckInJpaRepository;
import com.monthlychallenge.adapter.out.persistence.friendship.FriendshipJpaEntity;
import com.monthlychallenge.adapter.out.persistence.friendship.FriendshipJpaRepository;
import com.monthlychallenge.adapter.out.persistence.streak.StreakJpaRepository;
import com.monthlychallenge.adapter.out.persistence.user.UserJpaEntity;
import com.monthlychallenge.adapter.out.persistence.user.UserJpaRepository;
import com.monthlychallenge.application.dto.FriendFeedEntry;
import com.monthlychallenge.domain.enums.ChallengeVisibility;
import com.monthlychallenge.domain.enums.CheckInStatus;
import com.monthlychallenge.domain.models.Challenge;
import com.monthlychallenge.domain.models.CheckIn;
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
public class FriendsFeedService {

    private final FriendshipJpaRepository friendshipRepo;
    private final UserJpaRepository userRepo;
    private final ChallengeJpaRepository challengeRepo;
    private final CheckInJpaRepository checkInRepo;
    private final StreakJpaRepository streakRepo;

    public FriendsFeedService(FriendshipJpaRepository friendshipRepo,
                              UserJpaRepository userRepo,
                              ChallengeJpaRepository challengeRepo,
                              CheckInJpaRepository checkInRepo,
                              StreakJpaRepository streakRepo) {
        this.friendshipRepo = friendshipRepo;
        this.userRepo = userRepo;
        this.challengeRepo = challengeRepo;
        this.checkInRepo = checkInRepo;
        this.streakRepo = streakRepo;
    }

    public List<FriendFeedEntry> getFriendsFeed(UUID userId) {
        LocalDate today = LocalDate.now();
        String currentMonth = YearMonth.from(today).toString();

        List<FriendshipJpaEntity> friends = friendshipRepo.findAcceptedFriends(userId);

        return friends.stream().map(f -> {
            UUID friendId = f.getRequesterId().equals(userId) ? f.getAddresseeId() : f.getRequesterId();
            UserJpaEntity friend = userRepo.findById(friendId).orElse(null);
            if (friend == null) return null;

            int streak = streakRepo.findByUserId(friendId).map(s -> s.getCurrentStreak()).orElse(0);

            List<ChallengeJpaEntity> shared = challengeRepo.findByOwnerIdAndMonthAndActiveTrue(friendId, currentMonth)
                    .stream()
                    .filter(c -> !ChallengeVisibility.PRIVATE.name().equals(c.getVisibility()))
                    .toList();

            Map<UUID, CheckInJpaEntity> checkInsToday = checkInRepo.findByUserIdAndDate(friendId, today)
                    .stream().collect(Collectors.toMap(CheckInJpaEntity::getChallengeId, ci -> ci));

            int completedToday = 0;
            int halfCompletedToday = 0;

            List<FriendFeedEntry.ChallengeWithCheckIn> progressList = new java.util.ArrayList<>();
            for (ChallengeJpaEntity c : shared) {
                CheckInJpaEntity ci = checkInsToday.get(c.getId());
                CheckIn domainCi = null;
                if (ci != null) {
                    domainCi = CheckIn.builder()
                            .id(ci.getId()).userId(ci.getUserId()).challengeId(ci.getChallengeId())
                            .date(ci.getDate()).status(CheckInStatus.valueOf(ci.getStatus()))
                            .actualValue(ci.getActualValue()).build();
                    if ("COMPLETED".equals(ci.getStatus())) completedToday++;
                    if ("HALF_COMPLETED".equals(ci.getStatus())) halfCompletedToday++;
                }

                Challenge domainChallenge = Challenge.builder()
                        .id(c.getId()).ownerId(c.getOwnerId()).title(c.getTitle())
                        .category(com.monthlychallenge.domain.enums.ChallengeCategory.valueOf(c.getCategory()))
                        .build();

                progressList.add(new FriendFeedEntry.ChallengeWithCheckIn(domainChallenge, domainCi));
            }

            return new FriendFeedEntry(friendId, friend.getUsername(), friend.getDisplayName(),
                    friend.getProfilePhotoUrl(), streak, shared.size(), completedToday,
                    halfCompletedToday, progressList);

        }).filter(java.util.Objects::nonNull).toList();
    }
}
