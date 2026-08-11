package com.monthlychallenge.adapter.out.persistence.streak;

import com.monthlychallenge.adapter.out.persistence.streak.StreakJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StreakJpaRepository extends JpaRepository<StreakJpaEntity, UUID> {

    Optional<StreakJpaEntity> findByUserId(UUID userId);
}
