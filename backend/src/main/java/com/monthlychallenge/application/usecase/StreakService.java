package com.monthlychallenge.application.usecase;

import com.monthlychallenge.adapter.out.persistence.streak.StreakJpaRepository;
import com.monthlychallenge.domain.models.Streak;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class StreakService {

    private final StreakJpaRepository streakRepo;

    public StreakService(StreakJpaRepository streakRepo) {
        this.streakRepo = streakRepo;
    }

    public Streak getStreak(UUID userId) {
        return streakRepo.findByUserId(userId)
                .map(s -> Streak.builder()
                        .id(s.getId()).userId(s.getUserId())
                        .currentStreak(s.getCurrentStreak())
                        .longestStreak(s.getLongestStreak())
                        .lastSuccessDate(s.getLastSuccessDate())
                        .build())
                .orElseGet(() -> Streak.builder().id(UUID.randomUUID()).userId(userId)
                        .currentStreak(0).longestStreak(0).build());
    }
}
