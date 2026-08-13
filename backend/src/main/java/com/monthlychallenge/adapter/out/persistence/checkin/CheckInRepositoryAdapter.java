package com.monthlychallenge.adapter.out.persistence.checkin;

import com.monthlychallenge.application.dto.ChallengeCheckInSummary;
import com.monthlychallenge.application.ports.out.CheckInRepository;
import com.monthlychallenge.domain.enums.CheckInStatus;
import com.monthlychallenge.domain.models.CheckIn;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CheckInRepositoryAdapter implements CheckInRepository {

    private final CheckInJpaRepository jpaRepository;

    public CheckInRepositoryAdapter(CheckInJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CheckIn save(CheckIn checkIn) {
        return toDomain(jpaRepository.save(toEntity(checkIn)));
    }

    @Override
    public Optional<CheckIn> findByUserIdAndChallengeIdAndDate(UUID userId, UUID challengeId, LocalDate date) {
        return jpaRepository.findByUserIdAndChallengeIdAndDate(userId, challengeId, date).map(this::toDomain);
    }

    @Override
    public List<CheckIn> findByUserIdAndDate(UUID userId, LocalDate date) {
        return jpaRepository.findByUserIdAndDate(userId, date).stream().map(this::toDomain).toList();
    }

    @Override
    public List<CheckIn> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to) {
        return jpaRepository.findByUserIdAndDateBetween(userId, from, to).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ChallengeCheckInSummary> summariseByChallenge(UUID userId, LocalDate from, LocalDate to) {
        return jpaRepository.summariseByChallenge(userId, from, to).stream().map(row -> {
            UUID cId = (UUID) row[0];
            long comp = row[1] != null ? ((Number) row[1]).longValue() : 0;
            long half = row[2] != null ? ((Number) row[2]).longValue() : 0;
            long miss = row[3] != null ? ((Number) row[3]).longValue() : 0;
            return new ChallengeCheckInSummary(cId, comp, half, miss);
        }).toList();
    }

    private CheckIn toDomain(CheckInJpaEntity e) {
        return CheckIn.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .challengeId(e.getChallengeId())
                .date(e.getDate())
                .status(CheckInStatus.valueOf(e.getStatus()))
                .actualValue(e.getActualValue())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private CheckInJpaEntity toEntity(CheckIn ci) {
        CheckInJpaEntity e = new CheckInJpaEntity();
        e.setId(ci.getId());
        e.setUserId(ci.getUserId());
        e.setChallengeId(ci.getChallengeId());
        e.setDate(ci.getDate());
        e.setStatus(ci.getStatus() != null ? ci.getStatus().name() : null);
        e.setActualValue(ci.getActualValue());
        e.setCreatedAt(ci.getCreatedAt());
        e.setUpdatedAt(ci.getUpdatedAt());
        return e;
    }
}
