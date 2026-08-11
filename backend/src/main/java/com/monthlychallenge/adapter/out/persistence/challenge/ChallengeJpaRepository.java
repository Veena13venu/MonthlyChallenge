package com.monthlychallenge.adapter.out.persistence.challenge;

import com.monthlychallenge.adapter.out.persistence.challenge.ChallengeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChallengeJpaRepository extends JpaRepository<ChallengeJpaEntity, UUID> {

    Optional<ChallengeJpaEntity> findByIdAndOwnerIdAndActiveTrue(UUID id, UUID ownerId);

    List<ChallengeJpaEntity> findByOwnerIdAndMonthAndActiveTrue(UUID ownerId, String month);

    @Query("SELECT c FROM ChallengeJpaEntity c WHERE c.active = true AND c.reminderHour = :hour AND c.reminderMinute = :minute")
    List<ChallengeJpaEntity> findAllWithReminderAt(@Param("hour") int hour, @Param("minute") int minute);
}
