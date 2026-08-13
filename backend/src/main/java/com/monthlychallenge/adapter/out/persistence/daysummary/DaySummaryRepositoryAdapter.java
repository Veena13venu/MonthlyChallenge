package com.monthlychallenge.adapter.out.persistence.daysummary;

import com.monthlychallenge.application.ports.out.DaySummaryRepository;
import com.monthlychallenge.domain.enums.DayResult;
import com.monthlychallenge.domain.models.DaySummary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DaySummaryRepositoryAdapter implements DaySummaryRepository {

    private final DaySummaryJpaRepository jpaRepository;

    public DaySummaryRepositoryAdapter(DaySummaryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public DaySummary save(DaySummary daySummary) {
        return toDomain(jpaRepository.save(toEntity(daySummary)));
    }

    @Override
    public Optional<DaySummary> findByUserIdAndDate(UUID userId, LocalDate date) {
        return jpaRepository.findByUserIdAndDate(userId, date).map(this::toDomain);
    }

    @Override
    public List<DaySummary> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to) {
        return jpaRepository.findByUserIdAndDateBetween(userId, from, to).stream().map(this::toDomain).toList();
    }

    private DaySummary toDomain(DaySummaryJpaEntity e) {
        return DaySummary.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .date(e.getDate())
                .totalPoints(e.getTotalPoints())
                .minimumThreshold(e.getMinimumThreshold())
                .result(DayResult.valueOf(e.getResult()))
                .build();
    }

    private DaySummaryJpaEntity toEntity(DaySummary ds) {
        DaySummaryJpaEntity e = new DaySummaryJpaEntity();
        e.setId(ds.getId());
        e.setUserId(ds.getUserId());
        e.setDate(ds.getDate());
        e.setTotalPoints(ds.getTotalPoints());
        e.setMinimumThreshold(ds.getMinimumThreshold());
        e.setResult(ds.getResult() != null ? ds.getResult().name() : null);
        return e;
    }
}
