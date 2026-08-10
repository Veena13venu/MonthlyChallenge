package com.monthlychallenge.infrastructure.persistence.jpa;

import com.monthlychallenge.infrastructure.persistence.entity.DaySummaryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DaySummaryJpaRepository extends JpaRepository<DaySummaryJpaEntity, UUID> {

    Optional<DaySummaryJpaEntity> findByUserIdAndDate(UUID userId, LocalDate date);

    List<DaySummaryJpaEntity> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to);
}
