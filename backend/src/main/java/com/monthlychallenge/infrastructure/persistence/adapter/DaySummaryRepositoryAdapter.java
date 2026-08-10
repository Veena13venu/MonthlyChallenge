package com.monthlychallenge.infrastructure.persistence.adapter;

import com.monthlychallenge.application.port.out.DaySummaryRepository;
import com.monthlychallenge.domain.model.DayResult;
import com.monthlychallenge.domain.model.DaySummary;
import com.monthlychallenge.infrastructure.persistence.entity.DaySummaryJpaEntity;
import com.monthlychallenge.infrastructure.persistence.jpa.DaySummaryJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DaySummaryRepositoryAdapter implements DaySummaryRepository {

    private final DaySummaryJpaRepository jpa;

    public DaySummaryRepositoryAdapter(DaySummaryJpaRepository jpa) { this.jpa = jpa; }

    @Override public DaySummary save(DaySummary ds) { return toDomain(jpa.save(toEntity(ds))); }

    @Override
    public Optional<DaySummary> findByUserIdAndDate(UUID userId, LocalDate date) {
        return jpa.findByUserIdAndDate(userId, date).map(this::toDomain);
    }

    @Override
    public List<DaySummary> findByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to) {
        return jpa.findByUserIdAndDateBetween(userId, from, to).stream().map(this::toDomain).toList();
    }

    private DaySummary toDomain(DaySummaryJpaEntity e) {
        return DaySummary.builder()
                .id(e.getId()).userId(e.getUserId()).date(e.getDate())
                .totalPoints(e.getTotalPoints()).minimumThreshold(e.getMinimumThreshold())
                .result(DayResult.valueOf(e.getResult()))
                .build();
    }

    private DaySummaryJpaEntity toEntity(DaySummary ds) {
        return DaySummaryJpaEntity.builder()
                .id(ds.getId()).userId(ds.getUserId()).date(ds.getDate())
                .totalPoints(ds.getTotalPoints()).minimumThreshold(ds.getMinimumThreshold())
                .result(ds.getResult().name())
                .build();
    }
}
