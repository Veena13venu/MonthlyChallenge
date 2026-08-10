package com.monthlychallenge.infrastructure.persistence.jpa;

import com.monthlychallenge.infrastructure.persistence.entity.StreakJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StreakJpaRepository extends JpaRepository<StreakJpaEntity, UUID> {

    Optional<StreakJpaEntity> findByUserId(UUID userId);
}
