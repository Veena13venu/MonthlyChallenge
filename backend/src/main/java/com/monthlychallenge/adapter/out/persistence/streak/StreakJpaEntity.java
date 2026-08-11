package com.monthlychallenge.adapter.out.persistence.streak;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name = "streaks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StreakJpaEntity {

    @Id @Column(columnDefinition = "uuid") private UUID id;
    @Column(name = "user_id", nullable = false, columnDefinition = "uuid") private UUID userId;
    @Column(name = "current_streak", nullable = false) private int currentStreak;
    @Column(name = "longest_streak", nullable = false) private int longestStreak;
    @Column(name = "last_success_date") private LocalDate lastSuccessDate;
}
