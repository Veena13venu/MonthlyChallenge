package com.monthlychallenge.infrastructure.persistence.jpa;

import com.monthlychallenge.infrastructure.persistence.entity.CheckInJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CheckInJpaRepository extends JpaRepository<CheckInJpaEntity, UUID> {

    Optional<CheckInJpaEntity> findByUserIdAndChallengeIdAndDate(UUID userId, UUID challengeId, LocalDate date);

    List<CheckInJpaEntity> findByUserIdAndDate(UUID userId, LocalDate date);

    List<CheckInJpaEntity> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to);

    @Query("""
        SELECT c.challengeId,
               SUM(CASE WHEN c.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completedCount,
               SUM(CASE WHEN c.status = 'HALF_COMPLETED' THEN 1 ELSE 0 END) AS halfCompletedCount,
               SUM(CASE WHEN c.status = 'MISSED' THEN 1 ELSE 0 END) AS missedCount
        FROM CheckInJpaEntity c
        WHERE c.userId = :userId AND c.date BETWEEN :from AND :to
        GROUP BY c.challengeId
        """)
    List<Object[]> summariseByChallenge(@Param("userId") UUID userId,
                                        @Param("from") LocalDate from,
                                        @Param("to") LocalDate to);
}
