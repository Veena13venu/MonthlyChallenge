package com.monthlychallenge.adapter.in.rest;

import com.monthlychallenge.application.ports.in.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal webhook called by Keycloak's HTTP Event Listener when a user registers.
 * This ensures the user row is saved to PostgreSQL immediately at registration time,
 * rather than waiting for the user's first API call.
 *
 * Security: protected by a shared secret header (X-Hook-Secret) instead of JWT.
 * The secret is configured in both application.yml and Keycloak's event listener settings.
 */
@RestController
@RequestMapping("/v1/internal/keycloak")
@Tag(name = "Internal Webhooks", description = "Internal endpoints called by Keycloak — not for direct client use")
public class KeycloakWebhookController {

    private static final Logger log = LoggerFactory.getLogger(KeycloakWebhookController.class);
    private static final String HOOK_SECRET_HEADER = "X-Hook-Secret";

    private final UserUseCase userUseCase;

    @Value("${app.keycloak.hook-secret}")
    private String expectedSecret;

    public KeycloakWebhookController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    /**
     * Called by Keycloak immediately when a new user completes registration.
     * Saves the user to the local PostgreSQL database.
     */
    @PostMapping("/user-registered")
    @Operation(summary = "Keycloak registration webhook — saves new user to DB")
    public ResponseEntity<Void> onUserRegistered(
            @RequestHeader(HOOK_SECRET_HEADER) String hookSecret,
            @Valid @RequestBody KeycloakUserEvent event) {

        // Validate shared secret to prevent unauthorized calls
        if (!expectedSecret.equals(hookSecret)) {
            log.warn("Received Keycloak webhook with invalid secret — rejecting");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Keycloak registration webhook received for keycloakId={}, email={}",
                event.keycloakId(), event.email());

        userUseCase.provisionUserFromKeycloak(
                event.keycloakId(),
                event.email(),
                event.username()
        );

        log.info("User successfully provisioned in DB for keycloakId={}", event.keycloakId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Payload sent by Keycloak's HTTP Event Listener on REGISTER event.
     */
    public record KeycloakUserEvent(
            @NotBlank(message = "keycloakId is required") String keycloakId,
            @NotBlank(message = "email is required") String email,
            @NotBlank(message = "username is required") String username
    ) {}
}
