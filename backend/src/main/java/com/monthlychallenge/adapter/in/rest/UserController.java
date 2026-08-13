package com.monthlychallenge.adapter.in.rest;

import com.monthlychallenge.adapter.in.rest.dto.request.UpdateMinimumTargetRequest;
import com.monthlychallenge.adapter.in.rest.dto.request.UpdateProfileRequest;
import com.monthlychallenge.adapter.in.rest.mapper.UserWebMapper;
import com.monthlychallenge.application.dto.UserResponse;
import com.monthlychallenge.application.ports.in.UserUseCase;
import com.monthlychallenge.application.ports.in.command.UpdateMinimumTargetCommand;
import com.monthlychallenge.application.ports.in.command.UpdateProfileCommand;
import com.monthlychallenge.domain.models.User;
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

    private final UserUseCase userUseCase;
    private final UserWebMapper mapper;

    public UserController(UserUseCase userUseCase, UserWebMapper mapper) {
        this.userUseCase = userUseCase;
        this.mapper = mapper;
    }

    @GetMapping("/me")
    @Operation(summary = "Get my profile")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        User user = provision(jwt);
        return ResponseEntity.ok(mapper.toResponse(user));
    }

    @PutMapping("/me/profile")
    @Operation(summary = "Update display name and profile photo")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateProfileRequest req) {
        User user = provision(jwt);
        User updated = userUseCase.updateProfile(user.getId(),
                new UpdateProfileCommand(req.getDisplayName(), req.getProfilePhotoUrl()));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @PutMapping("/me/minimum-target")
    @Operation(summary = "Update minimum daily challenge target (FR-16)")
    public ResponseEntity<UserResponse> updateMinimumTarget(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateMinimumTargetRequest req) {
        User user = provision(jwt);
        User updated = userUseCase.updateMinimumDailyTarget(user.getId(),
                new UpdateMinimumTargetCommand(req.getValue(), req.isPercentage()));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by username prefix (FR-23)")
    public ResponseEntity<List<UserResponse>> search(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String q) {
        User me = provision(jwt);
        return ResponseEntity.ok(userUseCase.searchByUsername(q, me.getId())
                .stream().map(mapper::toResponse).toList());
    }

    private User provision(Jwt jwt) {
        AuthenticatedUser auth = AuthenticatedUser.from(jwt);
        return userUseCase.provisionUserFromKeycloak(
                auth.keycloakId(), auth.email(), auth.preferredUsername());
    }
}
