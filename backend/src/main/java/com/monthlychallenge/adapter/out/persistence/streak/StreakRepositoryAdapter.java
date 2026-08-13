package com.monthlychallenge.adapter.out.persistence.streak;

import com.monthlychallenge.application.ports.out.StreakRepository;
import com.monthlychallenge.domain.models.Streak;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class StreakRepositoryAdapter implements StreakRepository {

    private final StreakJpaRepository jpaRepository;

    public StreakRepositoryAdapter(StreakJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Streak save(Streak streak) {
        return toDomain(jpaRepository.save(toEntity(streak)));
    }

    @Override
    public Optional<Streak> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    private Streak toDomain(StreakJpaEntity e) {
        return Streak.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .currentStreak(e.getCurrentStreak())
                .longestStreak(e.getLongestStreak())
                .lastSuccessDate(e.getLastSuccessDate())
                .build();
    }

    private StreakJpaEntity toEntity(Streak s) {
        StreakJpaEntity e = new StreakJpaEntity();
        e.setId(s.getId());
        e.setUserId(s.getUserId());
        e.setCurrentStreak(s.getCurrentStreak());
        e.setLongestStreak(s.getLongestStreak());
        e.setLastSuccessDate(s.getLastSuccessDate());
        return e;
    }
}
