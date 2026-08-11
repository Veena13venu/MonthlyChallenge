package com.monthlychallenge.adapter.out.persistence.daysummary;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name = "day_summaries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DaySummaryJpaEntity {

    @Id @Column(columnDefinition = "uuid") private UUID id;
    @Column(name = "user_id", nullable = false, columnDefinition = "uuid") private UUID userId;
    @Column(nullable = false) private LocalDate date;
    @Column(name = "total_points", nullable = false) private double totalPoints;
    @Column(name = "minimum_threshold", nullable = false) private double minimumThreshold;
    @Column(nullable = false, length = 10) private String result;
}
