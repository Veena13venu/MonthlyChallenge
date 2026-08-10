package com.monthlychallenge.application.service;

import com.monthlychallenge.application.port.in.UserUseCase;
import com.monthlychallenge.application.port.in.command.UpdateMinimumTargetCommand;
import com.monthlychallenge.application.port.in.command.UpdateProfileCommand;
import com.monthlychallenge.application.port.out.UserRepository;
import com.monthlychallenge.domain.model.MinimumDailyTarget;
import com.monthlychallenge.domain.model.User;
import com.monthlychallenge.infrastructure.exception.ResourceNotFoundException;
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
            User newUser = User.builder()
                    .id(UUID.randomUUID()).keycloakId(keycloakId).email(email)
                    .username(username).displayName(preferredUsername)
                    .minimumDailyTarget(new MinimumDailyTarget(1, false))
                    .createdAt(Instant.now()).updatedAt(Instant.now())
                    .build();
            return userRepository.save(newUser);
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
        User updated = user
                .withDisplayName(command.displayName() != null ? command.displayName() : user.getDisplayName())
                .withProfilePhotoUrl(command.profilePhotoUrl() != null ? command.profilePhotoUrl() : user.getProfilePhotoUrl())
                .withUpdatedAt(Instant.now());
        return userRepository.save(updated);
    }

    @Override
    public User updateMinimumDailyTarget(UUID userId, UpdateMinimumTargetCommand command) {
        User user = getMyProfile(userId);
        return userRepository.save(user
                .withMinimumDailyTarget(new MinimumDailyTarget(command.value(), command.isPercentage()))
                .withUpdatedAt(Instant.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> searchByUsername(String usernameQuery, UUID excludeUserId) {
        return userRepository.searchByUsernameStartingWith(usernameQuery.toLowerCase(), excludeUserId);
    }

    private String resolveUniqueUsername(String preferred) {

        if (preferred == null || preferred.isBlank()) {
            preferred = "user";
        }

        String base = preferred
                .toLowerCase()
                .replaceAll("[^a-z0-9_]", "_");

        if (!userRepository.existsByUsername(base)) {
            return base;
        }

        for (int i = 1; i <= 99; i++) {
            String candidate = base + i;
            if (!userRepository.existsByUsername(candidate)) {
                return candidate;
            }
        }

        return base + UUID.randomUUID().toString().substring(0, 6);
    }
}
