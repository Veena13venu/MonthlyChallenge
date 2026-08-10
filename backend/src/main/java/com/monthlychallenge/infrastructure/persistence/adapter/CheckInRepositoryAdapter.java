package com.monthlychallenge.infrastructure.persistence.adapter;

import com.monthlychallenge.application.dto.ChallengeCheckInSummary;
import com.monthlychallenge.application.port.out.CheckInRepository;
import com.monthlychallenge.domain.model.CheckIn;
import com.monthlychallenge.domain.model.CheckInStatus;
import com.monthlychallenge.infrastructure.persistence.entity.CheckInJpaEntity;
import com.monthlychallenge.infrastructure.persistence.jpa.CheckInJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CheckInRepositoryAdapter implements CheckInRepository {

    private final CheckInJpaRepository jpa;

    public CheckInRepositoryAdapter(CheckInJpaRepository jpa) { this.jpa = jpa; }

    @Override public CheckIn save(CheckIn c) { return toDomain(jpa.save(toEntity(c))); }

    @Override
    public Optional<CheckIn> findByUserIdAndChallengeIdAndDate(UUID userId, UUID challengeId, LocalDate date) {
        return jpa.findByUserIdAndChallengeIdAndDate(userId, challengeId, date).map(this::toDomain);
    }

    @Override
    public List<CheckIn> findByUserIdAndDate(UUID userId, LocalDate date) {
        return jpa.findByUserIdAndDate(userId, date).stream().map(this::toDomain).toList();
    }

    @Override
    public List<CheckIn> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to) {
        return jpa.findByUserIdAndDateBetween(userId, from, to).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ChallengeCheckInSummary> summariseByChallenge(UUID userId, LocalDate from, LocalDate to) {
        return jpa.summariseByChallenge(userId, from, to).stream()
                .map(row -> new ChallengeCheckInSummary(
                        (UUID) row[0],
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).longValue()))
                .toList();
    }

    private CheckIn toDomain(CheckInJpaEntity e) {
        return CheckIn.builder()
                .id(e.getId()).challengeId(e.getChallengeId()).userId(e.getUserId())
                .date(e.getDate()).status(CheckInStatus.valueOf(e.getStatus()))
                .actualValue(e.getActualValue())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .build();
    }

    private CheckInJpaEntity toEntity(CheckIn c) {
        return CheckInJpaEntity.builder()
                .id(c.getId()).challengeId(c.getChallengeId()).userId(c.getUserId())
                .date(c.getDate()).status(c.getStatus().name()).actualValue(c.getActualValue())
                .createdAt(c.getCreatedAt()).updatedAt(c.getUpdatedAt())
                .build();
    }
}
