package com.monthlychallenge.adapter.out.persistence.user;

import com.monthlychallenge.application.ports.out.UserRepository;
import com.monthlychallenge.domain.models.MinimumDailyTarget;
import com.monthlychallenge.domain.models.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = toEntity(user);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByKeycloakId(String keycloakId) {
        return jpaRepository.findByKeycloakId(keycloakId).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public List<User> searchByUsernameStartingWith(String prefix, UUID excludeId) {
        if (excludeId == null) {
            return jpaRepository.searchByUsernameStartingWithNoExclude(prefix).stream().map(this::toDomain).toList();
        }
        return jpaRepository.searchByUsernameStartingWith(prefix, excludeId).stream().map(this::toDomain).toList();
    }

    private User toDomain(UserJpaEntity e) {
        return User.builder()
                .id(e.getId())
                .keycloakId(e.getKeycloakId())
                .username(e.getUsername())
                .displayName(e.getDisplayName())
                .email(e.getEmail())
                .profilePhotoUrl(e.getProfilePhotoUrl())
                .minimumDailyTarget(new MinimumDailyTarget(e.getMinimumTargetValue(), e.isMinimumTargetIsPercentage()))
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private UserJpaEntity toEntity(User u) {
        UserJpaEntity e = new UserJpaEntity();
        e.setId(u.getId());
        e.setKeycloakId(u.getKeycloakId());
        e.setUsername(u.getUsername());
        e.setDisplayName(u.getDisplayName());
        e.setEmail(u.getEmail());
        e.setProfilePhotoUrl(u.getProfilePhotoUrl());
        if (u.getMinimumDailyTarget() != null) {
            e.setMinimumTargetValue(u.getMinimumDailyTarget().getValue());
            e.setMinimumTargetIsPercentage(u.getMinimumDailyTarget().isPercentage());
        }
        e.setCreatedAt(u.getCreatedAt());
        e.setUpdatedAt(u.getUpdatedAt());
        return e;
    }
}
