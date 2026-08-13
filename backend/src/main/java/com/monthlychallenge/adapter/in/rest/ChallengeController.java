package com.monthlychallenge.adapter.in.rest;

import com.monthlychallenge.adapter.in.rest.dto.request.CreateChallengeRequest;
import com.monthlychallenge.adapter.in.rest.dto.request.UpdateChallengeRequest;
import com.monthlychallenge.adapter.in.rest.mapper.ChallengeWebMapper;
import com.monthlychallenge.application.dto.ChallengeResponse;
import com.monthlychallenge.application.dto.ChallengeTemplateResponse;
import com.monthlychallenge.application.ports.in.ChallengeUseCase;
import com.monthlychallenge.application.ports.in.UserUseCase;
import com.monthlychallenge.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/challenges")
@Tag(name = "Challenges", description = "Challenge creation and management (FR-01 to FR-10)")
public class ChallengeController {

    private final ChallengeUseCase challengeUseCase;
    private final UserUseCase userUseCase;
    private final ChallengeWebMapper mapper;

    public ChallengeController(ChallengeUseCase challengeUseCase,
                               UserUseCase userUseCase,
                               ChallengeWebMapper mapper) {
        this.challengeUseCase = challengeUseCase;
        this.userUseCase = userUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new challenge (FR-01 to FR-06)")
    public ResponseEntity<ChallengeResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateChallengeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(challengeUseCase.createChallenge(userId(jwt), mapper.toCreateCommand(req))));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing challenge (FR-07)")
    public ResponseEntity<ChallengeResponse> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateChallengeRequest req) {
        return ResponseEntity.ok(mapper.toResponse(challengeUseCase.updateChallenge(userId(jwt), id, mapper.toUpdateCommand(req))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a challenge (FR-07)")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        challengeUseCase.deleteChallenge(userId(jwt), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get active challenges for a month (FR-08)")
    public ResponseEntity<List<ChallengeResponse>> getForMonth(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam YearMonth month) {
        return ResponseEntity.ok(challengeUseCase.getChallengesForMonth(userId(jwt), month)
                .stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's due challenges")
    public ResponseEntity<List<ChallengeResponse>> getToday(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(challengeUseCase.getTodaysChallenges(userId(jwt))
                .stream().map(mapper::toResponse).toList());
    }

    @PostMapping("/rollover")
    @Operation(summary = "Copy active challenges from previous month into current month (FR-09)")
    public ResponseEntity<List<ChallengeResponse>> rollover(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam YearMonth fromMonth,
            @RequestParam YearMonth toMonth) {
        return ResponseEntity.ok(challengeUseCase.rolloverChallenges(userId(jwt), fromMonth, toMonth)
                .stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/templates")
    @Operation(summary = "Get pre-defined challenge templates (FR-10)")
    public ResponseEntity<List<ChallengeTemplateResponse>> templates() {
        return ResponseEntity.ok(challengeUseCase.getChallengeTemplates()
                .stream().map(mapper::toTemplateResponse).toList());
    }

    private UUID userId(Jwt jwt) {
        AuthenticatedUser auth = AuthenticatedUser.from(jwt);
        return userUseCase.provisionUserFromKeycloak(
                auth.keycloakId(), auth.email(), auth.preferredUsername()).getId();
    }
}
