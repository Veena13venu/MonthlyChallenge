package com.monthlychallenge.adapter.in.rest;

import com.monthlychallenge.application.usecase.UserService;
import com.monthlychallenge.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/device-tokens")
@Tag(name = "Device Tokens", description = "Push notification token registration")
public class DeviceTokenController {

    private final UserService userService;

    public DeviceTokenController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Register device token")
    public ResponseEntity<Void> register(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RegisterTokenRequest request) {
        AuthenticatedUser auth = AuthenticatedUser.from(jwt);
        userService.provisionUser(auth.keycloakId(), auth.email(), auth.preferredUsername());
        return ResponseEntity.noContent().build();
    }

    public record RegisterTokenRequest(@NotBlank(message = "fcmToken is required") String fcmToken) {}
}
