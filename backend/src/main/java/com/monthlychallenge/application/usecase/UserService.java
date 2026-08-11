package com.monthlychallenge.application.usecase;

import com.monthlychallenge.adapter.out.persistence.user.UserJpaEntity;
import com.monthlychallenge.adapter.out.persistence.user.UserJpaRepository;
import com.monthlychallenge.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserJpaRepository userRepo;

    public UserService(UserJpaRepository userRepo) {
        this.userRepo = userRepo;
    }

    /** Creates a new user on first login, or returns the existing one. */
    public UserJpaEntity provisionUser(String keycloakId, String email, String preferredUsername) {
        return userRepo.findByKeycloakId(keycloakId).orElseGet(() -> {
            String username = resolveUniqueUsername(preferredUsername);
            UserJpaEntity user = new UserJpaEntity();
            user.setId(UUID.randomUUID());
            user.setKeycloakId(keycloakId);
            user.setEmail(email);
            user.setUsername(username);
            user.setDisplayName(preferredUsername);
            user.setMinimumTargetValue(1.0);
            user.setMinimumTargetIsPercentage(false);
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(Instant.now());
            return userRepo.save(user);
        });
    }

    @Transactional(readOnly = true)
    public UserJpaEntity getMyProfile(UUID userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserJpaEntity updateProfile(UUID userId, String displayName, String profilePhotoUrl) {
        UserJpaEntity user = getMyProfile(userId);
        if (displayName != null) user.setDisplayName(displayName);
        if (profilePhotoUrl != null) user.setProfilePhotoUrl(profilePhotoUrl);
        user.setUpdatedAt(Instant.now());
        return userRepo.save(user);
    }

    public UserJpaEntity updateMinimumTarget(UUID userId, double value, boolean isPercentage) {
        UserJpaEntity user = getMyProfile(userId);
        user.setMinimumTargetValue(value);
        user.setMinimumTargetIsPercentage(isPercentage);
        user.setUpdatedAt(Instant.now());
        return userRepo.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserJpaEntity> searchByUsername(String query, UUID excludeId) {
        return userRepo.searchByUsernameStartingWith(query.toLowerCase(), excludeId);
    }

    private String resolveUniqueUsername(String preferred) {
        if (preferred == null || preferred.isBlank()) preferred = "user";
        String base = preferred.toLowerCase().replaceAll("[^a-z0-9_]", "_");
        if (!userRepo.existsByUsername(base)) return base;
        for (int i = 1; i <= 99; i++) {
            String candidate = base + i;
            if (!userRepo.existsByUsername(candidate)) return candidate;
        }
        return base + UUID.randomUUID().toString().substring(0, 6);
    }
}
