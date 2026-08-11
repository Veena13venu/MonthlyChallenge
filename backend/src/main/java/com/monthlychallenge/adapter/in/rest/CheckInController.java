package com.monthlychallenge.adapter.in.rest;

import com.monthlychallenge.adapter.in.rest.dto.request.RecordCheckInRequest;
import com.monthlychallenge.adapter.out.persistence.checkin.CheckInJpaEntity;
import com.monthlychallenge.application.dto.CheckInResponse;
import com.monthlychallenge.application.dto.DaySummaryResponse;
import com.monthlychallenge.application.usecase.CheckInService;
import com.monthlychallenge.application.usecase.UserService;
import com.monthlychallenge.domain.enums.CheckInStatus;
import com.monthlychallenge.domain.models.DaySummary;
import com.monthlychallenge.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/check-ins")
@Tag(name = "Check-ins", description = "Daily check-in recording and progress")
public class CheckInController {

    private final CheckInService checkInService;
    private final UserService userService;

    public CheckInController(CheckInService checkInService, UserService userService) {
        this.checkInService = checkInService;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Record or update a check-in (FR-11, FR-12, FR-13)")
    public ResponseEntity<CheckInResponse> record(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RecordCheckInRequest req) {
        UUID userId = userId(jwt);
        CheckInJpaEntity ci = checkInService.recordCheckIn(
                userId, req.getChallengeId(), req.getStatus(), req.getActualValue());
        return ResponseEntity.ok(toResponse(ci));
    }

    @GetMapping
    @Operation(summary = "Get all check-ins for a date")
    public ResponseEntity<List<CheckInResponse>> getForDate(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(checkInService.getCheckInsForDate(userId(jwt), date)
                .stream().map(this::toResponse).toList());
    }

    @GetMapping("/summary")
    @Operation(summary = "Get live day summary")
    public ResponseEntity<DaySummaryResponse> getSummary(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.now();
        DaySummary ds = checkInService.getLiveDaySummary(userId(jwt), target);
        return ResponseEntity.ok(new DaySummaryResponse(
                ds.getDate(), ds.getTotalPoints(), ds.getMinimumThreshold(), ds.getResult()));
    }

    private UUID userId(Jwt jwt) {
        AuthenticatedUser auth = AuthenticatedUser.from(jwt);
        return userService.provisionUser(auth.keycloakId(), auth.email(), auth.preferredUsername()).getId();
    }

    private CheckInResponse toResponse(CheckInJpaEntity ci) {
        return new CheckInResponse(ci.getId(), ci.getChallengeId(), ci.getDate(),
                CheckInStatus.valueOf(ci.getStatus()), ci.getActualValue(), ci.getPointValue());
    }
}
