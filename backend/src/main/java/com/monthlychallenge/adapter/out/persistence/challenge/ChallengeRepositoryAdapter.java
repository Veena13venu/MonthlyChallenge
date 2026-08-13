package com.monthlychallenge.adapter.out.persistence.challenge;

import com.monthlychallenge.application.ports.out.ChallengeRepository;
import com.monthlychallenge.domain.enums.ChallengeCategory;
import com.monthlychallenge.domain.enums.ChallengeFrequency;
import com.monthlychallenge.domain.enums.ChallengeVisibility;
import com.monthlychallenge.domain.models.Challenge;
import com.monthlychallenge.domain.models.ChallengeTarget;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ChallengeRepositoryAdapter implements ChallengeRepository {

    private final ChallengeJpaRepository jpaRepository;

    public ChallengeRepositoryAdapter(ChallengeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Challenge save(Challenge challenge) {
        return toDomain(jpaRepository.save(toEntity(challenge)));
    }

    @Override
    public Optional<Challenge> findByIdAndOwnerId(UUID id, UUID ownerId) {
        return jpaRepository.findByIdAndOwnerIdAndActiveTrue(id, ownerId).map(this::toDomain);
    }

    @Override
    public List<Challenge> findActiveByOwnerIdAndMonth(UUID ownerId, YearMonth month) {
        return jpaRepository.findByOwnerIdAndMonthAndActiveTrue(ownerId, month.toString())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Challenge> findAllWithReminderAt(int hour, int minute) {
        return jpaRepository.findAllWithReminderAt(hour, minute).stream().map(this::toDomain).toList();
    }

    private Challenge toDomain(ChallengeJpaEntity e) {
        ChallengeTarget target = null;
        if (e.getTargetValue() != null && !e.getTargetValue().isBlank()) {
            String[] parts = e.getTargetValue().split(":", 2);
            try {
                double val = Double.parseDouble(parts[0]);
                String unit = parts.length > 1 && !parts[1].isBlank() && !"null".equalsIgnoreCase(parts[1]) ? parts[1] : null;
                target = new ChallengeTarget(val, unit);
            } catch (Exception ignored) {}
        }

        Set<DayOfWeek> weeklyDays = e.getWeeklyDueDays() != null && !e.getWeeklyDueDays().isBlank()
                ? Arrays.stream(e.getWeeklyDueDays().split(",")).map(DayOfWeek::valueOf).collect(Collectors.toSet())
                : null;

        return Challenge.builder()
                .id(e.getId())
                .ownerId(e.getOwnerId())
                .title(e.getTitle())
                .description(e.getDescription())
                .category(ChallengeCategory.valueOf(e.getCategory()))
                .frequency(ChallengeFrequency.valueOf(e.getFrequency()))
                .target(target)
                .month(YearMonth.parse(e.getMonth()))
                .visibility(ChallengeVisibility.valueOf(e.getVisibility()))
                .reminderHour(e.getReminderHour())
                .reminderMinute(e.getReminderMinute())
                .weeklyDueDays(weeklyDays)
                .monthlyDueDay(e.getMonthlyDueDay())
                .active(e.isActive())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private ChallengeJpaEntity toEntity(Challenge c) {
        ChallengeJpaEntity e = new ChallengeJpaEntity();
        e.setId(c.getId());
        e.setOwnerId(c.getOwnerId());
        e.setTitle(c.getTitle());
        e.setDescription(c.getDescription());
        e.setCategory(c.getCategory() != null ? c.getCategory().name() : null);
        e.setFrequency(c.getFrequency() != null ? c.getFrequency().name() : null);
        e.setMonth(c.getMonth() != null ? c.getMonth().toString() : null);
        e.setVisibility(c.getVisibility() != null ? c.getVisibility().name() : "SHARED");

        if (c.getTarget() != null) {
            String tStr = c.getTarget().getUnit() != null && !c.getTarget().getUnit().isBlank()
                    ? c.getTarget().getValue() + ":" + c.getTarget().getUnit()
                    : String.valueOf(c.getTarget().getValue());
            e.setTargetValue(tStr);
        }

        e.setReminderHour(c.getReminderHour());
        e.setReminderMinute(c.getReminderMinute());
        e.setWeeklyDueDays(c.getWeeklyDueDays() != null ? c.getWeeklyDueDays().stream().map(Enum::name).collect(Collectors.joining(",")) : null);
        e.setMonthlyDueDay(c.getMonthlyDueDay());
        e.setActive(c.isActive());
        e.setCreatedAt(c.getCreatedAt());
        e.setUpdatedAt(c.getUpdatedAt());
        return e;
    }
}
