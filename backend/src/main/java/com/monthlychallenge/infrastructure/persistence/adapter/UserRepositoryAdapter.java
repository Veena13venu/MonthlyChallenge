package com.monthlychallenge.infrastructure.persistence.adapter;

import com.monthlychallenge.application.port.out.UserRepository;
import com.monthlychallenge.domain.model.MinimumDailyTarget;
import com.monthlychallenge.domain.model.User;
import com.monthlychallenge.infrastructure.persistence.entity.UserJpaEntity;
import com.monthlychallenge.infrastructure.persistence.jpa.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpa;

    public UserRepositoryAdapter(UserJpaRepository jpa) { this.jpa = jpa; }

    @Override public User save(User user) { return toDomain(jpa.save(toEntity(user))); }
    @Override public Optional<User> findById(UUID id) { return jpa.findById(id).map(this::toDomain); }
    @Override public Optional<User> findByKeycloakId(String k) { return jpa.findByKeycloakId(k).map(this::toDomain); }
    @Override public Optional<User> findByUsername(String u) { return jpa.findByUsername(u).map(this::toDomain); }
    @Override public boolean existsByUsername(String u) { return jpa.existsByUsername(u); }

    @Override
    public List<User> searchByUsernameStartingWith(String prefix, UUID excludeUserId) {
        return (excludeUserId == null
                ? jpa.searchByUsernameStartingWithNoExclude(prefix)
                : jpa.searchByUsernameStartingWith(prefix, excludeUserId))
                .stream().map(this::toDomain).toList();
    }

    private User toDomain(UserJpaEntity e) {
        String[] p = e.getMinimumDailyTarget().split(":");
        return User.builder()
                .id(e.getId()).keycloakId(e.getKeycloakId()).username(e.getUsername())
                .displayName(e.getDisplayName()).email(e.getEmail())
                .profilePhotoUrl(e.getProfilePhotoUrl())
                .minimumDailyTarget(new MinimumDailyTarget(Double.parseDouble(p[0]), Boolean.parseBoolean(p[1])))
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .build();
    }

    private UserJpaEntity toEntity(User u) {
        return UserJpaEntity.builder()
                .id(u.getId()).keycloakId(u.getKeycloakId()).username(u.getUsername())
                .displayName(u.getDisplayName()).email(u.getEmail())
                .profilePhotoUrl(u.getProfilePhotoUrl())
                .minimumDailyTarget(u.getMinimumDailyTarget().getValue() + ":" + u.getMinimumDailyTarget().isPercentage())
                .createdAt(u.getCreatedAt()).updatedAt(u.getUpdatedAt())
                .build();
    }
}
