package com.monthlychallenge.adapter.out.persistence.challenge;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "challenges")
@Getter @Setter @NoArgsConstructor
public class ChallengeJpaEntity {

    @Id @Column(columnDefinition = "uuid") private UUID id;
    @Column(name = "owner_id", nullable = false, columnDefinition = "uuid") private UUID ownerId;
    @Column(nullable = false, length = 200) private String title;
    @Column(columnDefinition = "text") private String description;
    @Column(nullable = false, length = 30) private String category;
    @Column(nullable = false, length = 20) private String frequency;
    @Column(name = "target_value", length = 100) private String targetValue;
    @Column(nullable = false, length = 7) private String month;
    @Column(nullable = false, length = 10) private String visibility;
    @JdbcTypeCode(SqlTypes.SMALLINT) @Column(name = "reminder_hour") private Integer reminderHour;
    @JdbcTypeCode(SqlTypes.SMALLINT) @Column(name = "reminder_minute") private Integer reminderMinute;
    @Column(name = "weekly_due_days", length = 100) private String weeklyDueDays;
    @JdbcTypeCode(SqlTypes.SMALLINT) @Column(name = "monthly_due_day") private Integer monthlyDueDay;
    @Column(nullable = false) private boolean active;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
}
