package com.monthlychallenge.infrastructure.web.controller;

import com.monthlychallenge.application.port.in.ChallengeUseCase;
import com.monthlychallenge.application.port.in.UserUseCase;
import com.monthlychallenge.infrastructure.security.AuthenticatedUser;
import com.monthlychallenge.infrastructure.web.dto.request.CreateChallengeRequest;
import com.monthlychallenge.infrastructure.web.dto.request.UpdateChallengeRequest;
import com.monthlychallenge.infrastructure.web.dto.response.ChallengeResponse;
import com.monthlychallenge.infrastructure.web.dto.response.ChallengeTemplateResponse;
import com.monthlychallenge.infrastructure.web.mapper.ChallengeWebMapper;
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
@Tag(name = "Challenges", description = "Challenge creation and management")
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
    @Operation(summary = "Create a new challenge (FR-05)")
    public ResponseEntity<ChallengeResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateChallengeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(challengeUseCase.createChallenge(userId(jwt), mapper.toCreateCommand(req))));
    }

    @GetMapping
    @Operation(summary = "List challenges for a given month")
    public ResponseEntity<List<ChallengeResponse>> list(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam YearMonth month) {
        return ResponseEntity.ok(challengeUseCase.getChallengesForMonth(userId(jwt), month)
                .stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's due challenges")
    public ResponseEntity<List<ChallengeResponse>> today(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(challengeUseCase.getTodaysChallenges(userId(jwt))
                .stream().map(mapper::toResponse).toList());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a challenge (FR-08)")
    public ResponseEntity<ChallengeResponse> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateChallengeRequest req) {
        return ResponseEntity.ok(mapper.toResponse(
                challengeUseCase.updateChallenge(userId(jwt), id, mapper.toUpdateCommand(req))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete (archive) a challenge (FR-08)")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        challengeUseCase.deleteChallenge(userId(jwt), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/rollover")
    @Operation(summary = "Carry forward challenges from previous month (FR-09)")
    public ResponseEntity<List<ChallengeResponse>> rollover(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam YearMonth fromMonth,
            @RequestParam YearMonth toMonth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(challengeUseCase.rolloverChallenges(userId(jwt), fromMonth, toMonth)
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
