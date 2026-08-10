package com.monthlychallenge.infrastructure.persistence.adapter;

import com.monthlychallenge.application.port.out.ChallengeRepository;
import com.monthlychallenge.domain.model.*;
import com.monthlychallenge.infrastructure.persistence.entity.ChallengeJpaEntity;
import com.monthlychallenge.infrastructure.persistence.jpa.ChallengeJpaRepository;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ChallengeRepositoryAdapter implements ChallengeRepository {

    private final ChallengeJpaRepository jpa;

    public ChallengeRepositoryAdapter(ChallengeJpaRepository jpa) { this.jpa = jpa; }

    @Override public Challenge save(Challenge c) { return toDomain(jpa.save(toEntity(c))); }

    @Override
    public Optional<Challenge> findByIdAndOwnerId(UUID id, UUID ownerId) {
        return jpa.findByIdAndOwnerIdAndActiveTrue(id, ownerId).map(this::toDomain);
    }

    @Override
    public List<Challenge> findActiveByOwnerIdAndMonth(UUID ownerId, YearMonth month) {
        return jpa.findByOwnerIdAndMonthAndActiveTrue(ownerId, month.toString())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Challenge> findAllWithReminderAt(int hour, int minute) {
        return jpa.findAllWithReminderAt(hour, minute).stream().map(this::toDomain).toList();
    }

    private Challenge toDomain(ChallengeJpaEntity e) {
        ChallengeTarget target = null;
        if (e.getTargetValue() != null) {
            String[] p = e.getTargetValue().split(":", 2);
            target = new ChallengeTarget(Double.parseDouble(p[0]), p.length > 1 ? p[1] : "");
        }
        Set<DayOfWeek> weeklyDays = null;
        if (e.getWeeklyDueDays() != null && !e.getWeeklyDueDays().isBlank()) {
            weeklyDays = Arrays.stream(e.getWeeklyDueDays().split(","))
                    .map(DayOfWeek::valueOf).collect(Collectors.toSet());
        }
        return Challenge.builder()
                .id(e.getId()).ownerId(e.getOwnerId()).title(e.getTitle())
                .description(e.getDescription())
                .category(ChallengeCategory.valueOf(e.getCategory()))
                .frequency(ChallengeFrequency.valueOf(e.getFrequency()))
                .target(target).month(YearMonth.parse(e.getMonth()))
                .visibility(ChallengeVisibility.valueOf(e.getVisibility()))
                .reminderHour(e.getReminderHour()).reminderMinute(e.getReminderMinute())
                .weeklyDueDays(weeklyDays).monthlyDueDay(e.getMonthlyDueDay())
                .active(e.isActive()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .build();
    }

    private ChallengeJpaEntity toEntity(Challenge c) {
        String targetStr = c.getTarget() != null
                ? c.getTarget().getValue() + ":" + c.getTarget().getUnit() : null;
        String weeklyStr = (c.getWeeklyDueDays() != null && !c.getWeeklyDueDays().isEmpty())
                ? c.getWeeklyDueDays().stream().map(DayOfWeek::name).collect(Collectors.joining(",")) : null;
        return ChallengeJpaEntity.builder()
                .id(c.getId()).ownerId(c.getOwnerId()).title(c.getTitle())
                .description(c.getDescription()).category(c.getCategory().name())
                .frequency(c.getFrequency().name()).targetValue(targetStr)
                .month(c.getMonth().toString()).visibility(c.getVisibility().name())
                .reminderHour(c.getReminderHour()).reminderMinute(c.getReminderMinute())
                .weeklyDueDays(weeklyStr).monthlyDueDay(c.getMonthlyDueDay())
                .active(c.isActive()).createdAt(c.getCreatedAt()).updatedAt(c.getUpdatedAt())
                .build();
    }
}
