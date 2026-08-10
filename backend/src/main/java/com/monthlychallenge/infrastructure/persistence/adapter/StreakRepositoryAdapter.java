package com.monthlychallenge.infrastructure.persistence.adapter;

import com.monthlychallenge.application.port.out.StreakRepository;
import com.monthlychallenge.domain.model.Streak;
import com.monthlychallenge.infrastructure.persistence.entity.StreakJpaEntity;
import com.monthlychallenge.infrastructure.persistence.jpa.StreakJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class StreakRepositoryAdapter implements StreakRepository {

    private final StreakJpaRepository jpa;

    public StreakRepositoryAdapter(StreakJpaRepository jpa) { this.jpa = jpa; }

    @Override public Streak save(Streak s) { return toDomain(jpa.save(toEntity(s))); }

    @Override
    public Optional<Streak> findByUserId(UUID userId) {
        return jpa.findByUserId(userId).map(this::toDomain);
    }

    private Streak toDomain(StreakJpaEntity e) {
        return Streak.builder()
                .id(e.getId()).userId(e.getUserId())
                .currentStreak(e.getCurrentStreak()).longestStreak(e.getLongestStreak())
                .lastSuccessDate(e.getLastSuccessDate())
                .build();
    }

    private StreakJpaEntity toEntity(Streak s) {
        return StreakJpaEntity.builder()
                .id(s.getId()).userId(s.getUserId())
                .currentStreak(s.getCurrentStreak()).longestStreak(s.getLongestStreak())
                .lastSuccessDate(s.getLastSuccessDate())
                .build();
    }
}
