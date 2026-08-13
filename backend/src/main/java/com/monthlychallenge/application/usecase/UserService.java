package com.monthlychallenge.application.usecase;

import com.monthlychallenge.application.ports.in.UserUseCase;
import com.monthlychallenge.application.ports.in.command.UpdateMinimumTargetCommand;
import com.monthlychallenge.application.ports.in.command.UpdateProfileCommand;
import com.monthlychallenge.application.ports.out.UserRepository;
import com.monthlychallenge.domain.exceptions.ResourceNotFoundException;
import com.monthlychallenge.domain.models.MinimumDailyTarget;
import com.monthlychallenge.domain.models.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UserUseCase {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User provisionUserFromKeycloak(String keycloakId, String email, String preferredUsername) {
        return userRepository.findByKeycloakId(keycloakId).orElseGet(() -> {
            String username = resolveUniqueUsername(preferredUsername);
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .keycloakId(keycloakId)
                    .email(email)
                    .username(username)
                    .displayName(preferredUsername)
                    .minimumDailyTarget(new MinimumDailyTarget(1.0, false))
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            return userRepository.save(user);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public User getMyProfile(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User updateProfile(UUID userId, UpdateProfileCommand command) {
        User user = getMyProfile(userId);
        if (command.displayName() != null) user.setDisplayName(command.displayName());
        if (command.profilePhotoUrl() != null) user.setProfilePhotoUrl(command.profilePhotoUrl());
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    @Override
    public User updateMinimumDailyTarget(UUID userId, UpdateMinimumTargetCommand command) {
        User user = getMyProfile(userId);
        user.setMinimumDailyTarget(new MinimumDailyTarget(command.value(), command.isPercentage()));
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> searchByUsername(String usernameQuery, UUID excludeUserId) {
        return userRepository.searchByUsernameStartingWith(usernameQuery.toLowerCase(), excludeUserId);
    }

    private String resolveUniqueUsername(String preferred) {
        if (preferred == null || preferred.isBlank()) preferred = "user";
        String base = preferred.toLowerCase().replaceAll("[^a-z0-9_]", "_");
        if (!userRepository.existsByUsername(base)) return base;
        for (int i = 1; i <= 99; i++) {
            String candidate = base + i;
            if (!userRepository.existsByUsername(candidate)) return candidate;
        }
        return base + UUID.randomUUID().toString().substring(0, 6);
    }
}
