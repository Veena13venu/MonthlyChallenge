package com.monthlychallenge.adapter.out.persistence.friendship;

import com.monthlychallenge.application.ports.out.FriendshipRepository;
import com.monthlychallenge.domain.enums.FriendshipStatus;
import com.monthlychallenge.domain.models.Friendship;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class FriendshipRepositoryAdapter implements FriendshipRepository {

    private final FriendshipJpaRepository jpaRepository;

    public FriendshipRepositoryAdapter(FriendshipJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Friendship save(Friendship friendship) {
        return toDomain(jpaRepository.save(toEntity(friendship)));
    }

    @Override
    public Optional<Friendship> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Friendship> findBetween(UUID userA, UUID userB) {
        return jpaRepository.findBetween(userA, userB).map(this::toDomain);
    }

    @Override
    public Optional<Friendship> findBetweenAnyStatus(UUID userA, UUID userB) {
        return jpaRepository.findBetweenAnyStatus(userA, userB).map(this::toDomain);
    }

    @Override
    public List<Friendship> findByUserIdAndStatus(UUID userId, FriendshipStatus status) {
        return jpaRepository.findByUserIdAndStatus(userId, status.name()).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Friendship> findAcceptedFriends(UUID userId) {
        return jpaRepository.findAcceptedFriends(userId).stream().map(this::toDomain).toList();
    }

    private Friendship toDomain(FriendshipJpaEntity e) {
        return Friendship.builder()
                .id(e.getId())
                .requesterId(e.getRequesterId())
                .addresseeId(e.getAddresseeId())
                .status(FriendshipStatus.valueOf(e.getStatus()))
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private FriendshipJpaEntity toEntity(Friendship f) {
        FriendshipJpaEntity e = new FriendshipJpaEntity();
        e.setId(f.getId());
        e.setRequesterId(f.getRequesterId());
        e.setAddresseeId(f.getAddresseeId());
        e.setStatus(f.getStatus() != null ? f.getStatus().name() : null);
        e.setCreatedAt(f.getCreatedAt());
        e.setUpdatedAt(f.getUpdatedAt());
        return e;
    }
}
