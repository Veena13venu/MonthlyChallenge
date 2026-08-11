package com.monthlychallenge.adapter.out.persistence.friendship;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "friendships")
@Getter @Setter @NoArgsConstructor
public class FriendshipJpaEntity {

    @Id @Column(columnDefinition = "uuid") private UUID id;
    @Column(name = "requester_id", nullable = false, columnDefinition = "uuid") private UUID requesterId;
    @Column(name = "addressee_id", nullable = false, columnDefinition = "uuid") private UUID addresseeId;
    @Column(nullable = false, length = 10) private String status;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
}
