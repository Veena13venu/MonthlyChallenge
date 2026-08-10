package com.monthlychallenge.infrastructure.web.controller;

import com.monthlychallenge.application.port.in.UserUseCase;
import com.monthlychallenge.application.port.out.NotificationPort;
import com.monthlychallenge.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/device-tokens")
@Tag(name = "Device Tokens", description = "FCM push notification token registration")
public class DeviceTokenController {

    private final NotificationPort notificationPort;
    private final UserUseCase userUseCase;

    public DeviceTokenController(NotificationPort notificationPort, UserUseCase userUseCase) {
        this.notificationPort = notificationPort;
        this.userUseCase = userUseCase;
    }

    @PostMapping
    @Operation(summary = "Register or refresh the FCM device token for push notifications")
    public ResponseEntity<Void> register(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RegisterTokenRequest request) {
        AuthenticatedUser auth = AuthenticatedUser.from(jwt);
        UUID userId = userUseCase.provisionUserFromKeycloak(
                auth.keycloakId(), auth.email(), auth.preferredUsername()).getId();
        notificationPort.registerDeviceToken(userId, request.fcmToken());
        return ResponseEntity.noContent().build();
    }

    public record RegisterTokenRequest(@NotBlank(message = "fcmToken is required") String fcmToken) {}
}
