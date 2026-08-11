package com.monthlychallenge.adapter.in.rest;

import com.monthlychallenge.adapter.in.rest.dto.request.CreateChallengeRequest;
import com.monthlychallenge.adapter.in.rest.dto.request.UpdateChallengeRequest;
import com.monthlychallenge.adapter.out.persistence.challenge.ChallengeJpaEntity;
import com.monthlychallenge.application.dto.ChallengeResponse;
import com.monthlychallenge.application.dto.ChallengeTemplateResponse;
import com.monthlychallenge.application.usecase.ChallengeService;
import com.monthlychallenge.application.usecase.UserService;
import com.monthlychallenge.domain.enums.ChallengeCategory;
import com.monthlychallenge.domain.enums.ChallengeFrequency;
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

    private final ChallengeService challengeService;
    private final UserService userService;

    public ChallengeController(ChallengeService challengeService, UserService userService) {
        this.challengeService = challengeService;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create a new challenge (FR-01 to FR-06)")
    public ResponseEntity<ChallengeResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateChallengeRequest req) {
        ChallengeJpaEntity c = challengeService.createChallenge(userId(jwt), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(c));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing challenge (FR-07)")
    public ResponseEntity<ChallengeResponse> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateChallengeRequest req) {
        ChallengeJpaEntity c = challengeService.updateChallenge(userId(jwt), id, req);
        return ResponseEntity.ok(toResponse(c));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a challenge (FR-07)")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        challengeService.deleteChallenge(userId(jwt), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get active challenges for a month (FR-08)")
    public ResponseEntity<List<ChallengeResponse>> getForMonth(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String month) {
        return ResponseEntity.ok(challengeService.getChallengesForMonth(userId(jwt), month)
                .stream().map(this::toResponse).toList());
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's due challenges")
    public ResponseEntity<List<ChallengeResponse>> getToday(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(challengeService.getTodaysChallenges(userId(jwt))
                .stream().map(this::toResponse).toList());
    }

    @PostMapping("/rollover")
    @Operation(summary = "Copy active challenges from previous month into current month (FR-09)")
    public ResponseEntity<List<ChallengeResponse>> rollover(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String fromMonth,
            @RequestParam String toMonth) {
        return ResponseEntity.ok(challengeService.rolloverChallenges(userId(jwt), fromMonth, toMonth)
                .stream().map(this::toResponse).toList());
    }

    @GetMapping("/templates")
    @Operation(summary = "Get pre-defined challenge templates (FR-10)")
    public ResponseEntity<List<ChallengeTemplateResponse>> templates() {
        List<ChallengeTemplateResponse> list = List.of(
                new ChallengeTemplateResponse(UUID.randomUUID(), "Drink 3L Water", null, ChallengeCategory.HEALTH, ChallengeFrequency.DAILY, 3.0, "Litres"),
                new ChallengeTemplateResponse(UUID.randomUUID(), "Sleep 7 Hours", null, ChallengeCategory.SLEEP, ChallengeFrequency.DAILY, 7.0, "hours"),
                new ChallengeTemplateResponse(UUID.randomUUID(), "Walk 5 km", null, ChallengeCategory.FITNESS, ChallengeFrequency.DAILY, 5.0, "km"),
                new ChallengeTemplateResponse(UUID.randomUUID(), "Read 10 Pages", null, ChallengeCategory.LEARNING, ChallengeFrequency.DAILY, 10.0, "pages"),
                new ChallengeTemplateResponse(UUID.randomUUID(), "Meditate 10 min", null, ChallengeCategory.MINDFULNESS, ChallengeFrequency.DAILY, 10.0, "minutes"),
                new ChallengeTemplateResponse(UUID.randomUUID(), "Exercise 30 min", null, ChallengeCategory.FITNESS, ChallengeFrequency.DAILY, 30.0, "minutes"),
                new ChallengeTemplateResponse(UUID.randomUUID(), "No Junk Food", null, ChallengeCategory.NUTRITION, ChallengeFrequency.DAILY, null, null)
        );
        return ResponseEntity.ok(list);
    }

    private UUID userId(Jwt jwt) {
        AuthenticatedUser auth = AuthenticatedUser.from(jwt);
        return userService.provisionUser(auth.keycloakId(), auth.email(), auth.preferredUsername()).getId();
    }

    private ChallengeResponse toResponse(ChallengeJpaEntity c) {
        Double val = null;
        String unit = null;
        if (c.getTargetValue() != null && !c.getTargetValue().isBlank()) {
            String[] parts = c.getTargetValue().split(":", 2);
            try {
                val = Double.parseDouble(parts[0]);
                if (parts.length > 1 && !parts[1].isBlank() && !"null".equalsIgnoreCase(parts[1])) {
                    unit = parts[1];
                }
            } catch (NumberFormatException ignored) {}
        }
        java.util.Set<java.time.DayOfWeek> weeklyDays = c.getWeeklyDueDays() != null && !c.getWeeklyDueDays().isBlank()
                ? java.util.Arrays.stream(c.getWeeklyDueDays().split(","))
                        .map(java.time.DayOfWeek::valueOf)
                        .collect(java.util.stream.Collectors.toSet())
                : null;
        return new ChallengeResponse(c.getId(), c.getTitle(), c.getDescription(),
                com.monthlychallenge.domain.enums.ChallengeCategory.valueOf(c.getCategory()),
                com.monthlychallenge.domain.enums.ChallengeFrequency.valueOf(c.getFrequency()),
                YearMonth.parse(c.getMonth()),
                com.monthlychallenge.domain.enums.ChallengeVisibility.valueOf(c.getVisibility()),
                val, unit, c.getReminderHour(), c.getReminderMinute(),
                weeklyDays, c.getMonthlyDueDay(), c.isActive());
    }
}
