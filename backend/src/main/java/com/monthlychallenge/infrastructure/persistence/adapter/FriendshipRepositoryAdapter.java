package com.monthlychallenge.infrastructure.persistence.adapter;

import com.monthlychallenge.application.port.out.FriendshipRepository;
import com.monthlychallenge.domain.model.Friendship;
import com.monthlychallenge.domain.model.FriendshipStatus;
import com.monthlychallenge.infrastructure.persistence.entity.FriendshipJpaEntity;
import com.monthlychallenge.infrastructure.persistence.jpa.FriendshipJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class FriendshipRepositoryAdapter implements FriendshipRepository {

    private final FriendshipJpaRepository jpa;

    public FriendshipRepositoryAdapter(FriendshipJpaRepository jpa) { this.jpa = jpa; }

    @Override public Friendship save(Friendship f) { return toDomain(jpa.save(toEntity(f))); }
    @Override public Optional<Friendship> findById(UUID id) { return jpa.findById(id).map(this::toDomain); }
    @Override public Optional<Friendship> findBetween(UUID a, UUID b) { return jpa.findBetween(a, b).map(this::toDomain); }
    @Override public Optional<Friendship> findBetweenAnyStatus(UUID a, UUID b) { return jpa.findBetweenAnyStatus(a, b).map(this::toDomain); }
    @Override public List<Friendship> findAcceptedFriends(UUID userId) { return jpa.findAcceptedFriends(userId).stream().map(this::toDomain).toList(); }
    @Override public List<Friendship> findByUserIdAndStatus(UUID userId, FriendshipStatus status) {
        return jpa.findByUserIdAndStatus(userId, status.name()).stream().map(this::toDomain).toList();
    }

    private Friendship toDomain(FriendshipJpaEntity e) {
        return Friendship.builder()
                .id(e.getId()).requesterId(e.getRequesterId()).addresseeId(e.getAddresseeId())
                .status(FriendshipStatus.valueOf(e.getStatus()))
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .build();
    }

    private FriendshipJpaEntity toEntity(Friendship f) {
        return FriendshipJpaEntity.builder()
                .id(f.getId()).requesterId(f.getRequesterId()).addresseeId(f.getAddresseeId())
                .status(f.getStatus().name())
                .createdAt(f.getCreatedAt()).updatedAt(f.getUpdatedAt())
                .build();
    }
}
