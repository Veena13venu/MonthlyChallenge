package com.monthlychallenge.adapter.in.rest;

import com.monthlychallenge.adapter.in.rest.dto.request.UpdateMinimumTargetRequest;
import com.monthlychallenge.adapter.in.rest.dto.request.UpdateProfileRequest;
import com.monthlychallenge.adapter.out.persistence.user.UserJpaEntity;
import com.monthlychallenge.application.dto.UserResponse;
import com.monthlychallenge.application.usecase.UserService;
import com.monthlychallenge.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@Tag(name = "Users", description = "User profile management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get my profile")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        UserJpaEntity user = provision(jwt);
        return ResponseEntity.ok(toResponse(user));
    }

    @PutMapping("/me/profile")
    @Operation(summary = "Update display name and profile photo")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateProfileRequest req) {
        UserJpaEntity user = provision(jwt);
        UserJpaEntity updated = userService.updateProfile(user.getId(), req.getDisplayName(), req.getProfilePhotoUrl());
        return ResponseEntity.ok(toResponse(updated));
    }

    @PutMapping("/me/minimum-target")
    @Operation(summary = "Update minimum daily challenge target (FR-16)")
    public ResponseEntity<UserResponse> updateMinimumTarget(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateMinimumTargetRequest req) {
        UserJpaEntity user = provision(jwt);
        UserJpaEntity updated = userService.updateMinimumTarget(user.getId(), req.getValue(), req.isPercentage());
        return ResponseEntity.ok(toResponse(updated));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by username prefix (FR-23)")
    public ResponseEntity<List<UserResponse>> search(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String q) {
        UserJpaEntity me = provision(jwt);
        return ResponseEntity.ok(userService.searchByUsername(q, me.getId())
                .stream().map(this::toResponse).toList());
    }

    private UserJpaEntity provision(Jwt jwt) {
        AuthenticatedUser auth = AuthenticatedUser.from(jwt);
        return userService.provisionUser(auth.keycloakId(), auth.email(), auth.preferredUsername());
    }

    private UserResponse toResponse(UserJpaEntity u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getDisplayName(), u.getEmail(),
                u.getProfilePhotoUrl(), u.getMinimumTargetValue(), u.isMinimumTargetIsPercentage());
    }
}
