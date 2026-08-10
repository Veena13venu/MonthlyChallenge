package com.monthlychallenge.application.service;

import com.monthlychallenge.application.port.in.StreakUseCase;
import com.monthlychallenge.application.port.out.StreakRepository;
import com.monthlychallenge.domain.model.Streak;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class StreakService implements StreakUseCase {

    private final StreakRepository streakRepository;

    public StreakService(StreakRepository streakRepository) {
        this.streakRepository = streakRepository;
    }

    @Override
    public Streak getStreak(UUID userId) {
        return streakRepository.findByUserId(userId).orElseGet(() ->
                Streak.builder().id(UUID.randomUUID()).userId(userId)
                        .currentStreak(0).longestStreak(0).build());
    }
}
