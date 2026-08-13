package com.monthlychallenge.application.ports.in;

import com.monthlychallenge.application.ports.in.command.UpdateMinimumTargetCommand;
import com.monthlychallenge.application.ports.in.command.UpdateProfileCommand;
import com.monthlychallenge.domain.models.User;

import java.util.List;
import java.util.UUID;

public interface UserUseCase {
    User provisionUserFromKeycloak(String keycloakId, String email, String preferredUsername);
    User getMyProfile(UUID userId);
    User updateProfile(UUID userId, UpdateProfileCommand command);
    User updateMinimumDailyTarget(UUID userId, UpdateMinimumTargetCommand command);
    List<User> searchByUsername(String usernameQuery, UUID excludeUserId);
}
