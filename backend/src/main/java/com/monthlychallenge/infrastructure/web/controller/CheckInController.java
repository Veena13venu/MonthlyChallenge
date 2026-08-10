package com.monthlychallenge.infrastructure.web.controller;

import com.monthlychallenge.application.port.in.CheckInUseCase;
import com.monthlychallenge.application.port.in.UserUseCase;
import com.monthlychallenge.application.port.in.command.RecordCheckInCommand;
import com.monthlychallenge.infrastructure.security.AuthenticatedUser;
import com.monthlychallenge.infrastructure.web.dto.request.RecordCheckInRequest;
import com.monthlychallenge.infrastructure.web.dto.response.CheckInResponse;
import com.monthlychallenge.infrastructure.web.dto.response.DaySummaryResponse;
import com.monthlychallenge.infrastructure.web.mapper.CheckInWebMapper;
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

    private final CheckInUseCase checkInUseCase;
    private final UserUseCase userUseCase;
    private final CheckInWebMapper mapper;

    public CheckInController(CheckInUseCase checkInUseCase,
                              UserUseCase userUseCase,
                              CheckInWebMapper mapper) {
        this.checkInUseCase = checkInUseCase;
        this.userUseCase = userUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Record or update a check-in (FR-11, FR-12, FR-13)")
    public ResponseEntity<CheckInResponse> record(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RecordCheckInRequest req) {
        UUID userId = userId(jwt);
        return ResponseEntity.ok(mapper.toResponse(checkInUseCase.recordCheckIn(userId,
                new RecordCheckInCommand(req.getChallengeId(), req.getStatus(), req.getActualValue()))));
    }

    @GetMapping
    @Operation(summary = "Get all check-ins for a date")
    public ResponseEntity<List<CheckInResponse>> getForDate(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(checkInUseCase.getCheckInsForDate(userId(jwt), date)
                .stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/summary")
    @Operation(summary = "Get live day summary")
    public ResponseEntity<DaySummaryResponse> getSummary(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(mapper.toDaySummaryResponse(
                checkInUseCase.getLiveDaySummary(userId(jwt), target)));
    }

    private UUID userId(Jwt jwt) {
        AuthenticatedUser auth = AuthenticatedUser.from(jwt);
        return userUseCase.provisionUserFromKeycloak(
                auth.keycloakId(), auth.email(), auth.preferredUsername()).getId();
    }
}
