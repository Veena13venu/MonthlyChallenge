package com.monthlychallenge.adapter.in.rest;

import com.monthlychallenge.adapter.in.rest.mapper.CheckInWebMapper;
import com.monthlychallenge.adapter.in.rest.mapper.FriendsFeedWebMapper;
import com.monthlychallenge.application.dto.ChallengeCompletionRate;
import com.monthlychallenge.application.dto.DaySummaryResponse;
import com.monthlychallenge.application.dto.FriendFeedEntry;
import com.monthlychallenge.application.dto.FriendFeedResponse;
import com.monthlychallenge.application.dto.StreakResponse;
import com.monthlychallenge.application.ports.in.DashboardUseCase;
import com.monthlychallenge.application.ports.in.FriendsFeedUseCase;
import com.monthlychallenge.application.ports.in.StreakUseCase;
import com.monthlychallenge.application.ports.in.UserUseCase;
import com.monthlychallenge.domain.models.Streak;
import com.monthlychallenge.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/dashboard")
@Tag(name = "Dashboard", description = "Progress, history, streak, and friends feed")
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;
    private final FriendsFeedUseCase friendsFeedUseCase;
    private final StreakUseCase streakUseCase;
    private final UserUseCase userUseCase;
    private final CheckInWebMapper checkInWebMapper;
    private final FriendsFeedWebMapper friendsFeedWebMapper;

    public DashboardController(DashboardUseCase dashboardUseCase,
                                FriendsFeedUseCase friendsFeedUseCase,
                                StreakUseCase streakUseCase,
                                UserUseCase userUseCase,
                                CheckInWebMapper checkInWebMapper,
                                FriendsFeedWebMapper friendsFeedWebMapper) {
        this.dashboardUseCase = dashboardUseCase;
        this.friendsFeedUseCase = friendsFeedUseCase;
        this.streakUseCase = streakUseCase;
        this.userUseCase = userUseCase;
        this.checkInWebMapper = checkInWebMapper;
        this.friendsFeedWebMapper = friendsFeedWebMapper;
    }

    @GetMapping("/calendar")
    @Operation(summary = "Monthly calendar view — daily results (FR-36)")
    public ResponseEntity<List<DaySummaryResponse>> calendar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam YearMonth month) {
        return ResponseEntity.ok(dashboardUseCase.getMonthlyCalendar(userId(jwt), month)
                .stream().map(checkInWebMapper::toDaySummaryResponse).toList());
    }

    @GetMapping("/completion-rates")
    @Operation(summary = "Per-challenge completion rates for the month (FR-37)")
    public ResponseEntity<List<ChallengeCompletionRate>> completionRates(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam YearMonth month) {
        return ResponseEntity.ok(dashboardUseCase.getMonthlyCompletionRates(userId(jwt), month));
    }

    @GetMapping("/streak")
    @Operation(summary = "Current and longest streak (FR-19, FR-20)")
    public ResponseEntity<StreakResponse> streak(@AuthenticationPrincipal Jwt jwt) {
        Streak s = streakUseCase.getStreak(userId(jwt));
        return ResponseEntity.ok(new StreakResponse(s.getCurrentStreak(), s.getLongestStreak(), s.getLastSuccessDate()));
    }

    @GetMapping("/friends-feed")
    @Operation(summary = "Friends tab feed (FR-31)")
    public ResponseEntity<List<FriendFeedResponse>> friendsFeed(@AuthenticationPrincipal Jwt jwt) {
        List<FriendFeedEntry> feed = friendsFeedUseCase.getFriendsFeed(userId(jwt));
        return ResponseEntity.ok(feed.stream().map(friendsFeedWebMapper::toResponse).toList());
    }

    private UUID userId(Jwt jwt) {
        AuthenticatedUser auth = AuthenticatedUser.from(jwt);
        return userUseCase.provisionUserFromKeycloak(
                auth.keycloakId(), auth.email(), auth.preferredUsername()).getId();
    }
}
