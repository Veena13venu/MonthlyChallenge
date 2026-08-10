package com.monthlychallenge.application.port.in;

import com.monthlychallenge.application.port.in.command.UpdateProfileCommand;
import com.monthlychallenge.application.port.in.command.UpdateMinimumTargetCommand;
import com.monthlychallenge.domain.model.User;

import java.util.UUID;

/**
 * Inbound port — user profile management use cases (FR-03).
 */
public interface UserUseCase {

    /** Provisions a user profile the first time they log in via Keycloak. */
    User provisionUserFromKeycloak(String keycloakId, String email, String preferredUsername);

    /** Returns the currently authenticated user's profile. */
    User getMyProfile(UUID userId);

    /** Updates display name and/or profile photo. */
    User updateProfile(UUID userId, UpdateProfileCommand command);

    /** Updates the minimum daily completion target (FR-16). */
    User updateMinimumDailyTarget(UUID userId, UpdateMinimumTargetCommand command);

    /** Searches users by username prefix for the friend-search flow (FR-23). */
    java.util.List<User> searchByUsername(String usernameQuery, UUID excludeUserId);
}
