package com.monthlychallenge.adapter.in.rest;

import com.monthlychallenge.application.dto.ChallengeCompletionRate;
import com.monthlychallenge.application.dto.DaySummaryResponse;
import com.monthlychallenge.application.dto.FriendFeedEntry;
import com.monthlychallenge.application.dto.FriendFeedResponse;
import com.monthlychallenge.application.dto.StreakResponse;
import com.monthlychallenge.application.usecase.DashboardService;
import com.monthlychallenge.application.usecase.FriendsFeedService;
import com.monthlychallenge.application.usecase.StreakService;
import com.monthlychallenge.application.usecase.UserService;
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

    private final DashboardService dashboardService;
    private final FriendsFeedService friendsFeedService;
    private final StreakService streakService;
    private final UserService userService;

    public DashboardController(DashboardService dashboardService,
                                FriendsFeedService friendsFeedService,
                                StreakService streakService,
                                UserService userService) {
        this.dashboardService = dashboardService;
        this.friendsFeedService = friendsFeedService;
        this.streakService = streakService;
        this.userService = userService;
    }

    @GetMapping("/calendar")
    @Operation(summary = "Monthly calendar view — daily results (FR-36)")
    public ResponseEntity<List<DaySummaryResponse>> calendar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam YearMonth month) {
        return ResponseEntity.ok(dashboardService.getMonthlyCalendar(userId(jwt), month).stream()
                .map(ds -> new DaySummaryResponse(ds.getDate(), ds.getTotalPoints(), ds.getMinimumThreshold(), ds.getResult()))
                .toList());
    }

    @GetMapping("/completion-rates")
    @Operation(summary = "Per-challenge completion rates for the month (FR-37)")
    public ResponseEntity<List<ChallengeCompletionRate>> completionRates(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam YearMonth month) {
        return ResponseEntity.ok(dashboardService.getMonthlyCompletionRates(userId(jwt), month));
    }

    @GetMapping("/streak")
    @Operation(summary = "Current and longest streak (FR-19, FR-20)")
    public ResponseEntity<StreakResponse> streak(@AuthenticationPrincipal Jwt jwt) {
        Streak s = streakService.getStreak(userId(jwt));
        return ResponseEntity.ok(new StreakResponse(s.getCurrentStreak(), s.getLongestStreak(), s.getLastSuccessDate()));
    }

    @GetMapping("/friends-feed")
    @Operation(summary = "Friends tab feed (FR-31)")
    public ResponseEntity<List<FriendFeedResponse>> friendsFeed(@AuthenticationPrincipal Jwt jwt) {
        List<FriendFeedEntry> feed = friendsFeedService.getFriendsFeed(userId(jwt));
        List<FriendFeedResponse> resp = feed.stream().map(entry -> {
            List<FriendFeedResponse.FriendChallengeEntry> challenges = entry.sharedChallenges()
                    .stream()
                    .map(c -> new FriendFeedResponse.FriendChallengeEntry(
                            c.challenge().getId(),
                            c.challenge().getTitle(),
                            c.todaysCheckIn() != null ? c.todaysCheckIn().getStatus() : null))
                    .toList();

            return new FriendFeedResponse(
                    entry.friendUserId(), entry.username(), entry.displayName(),
                    entry.profilePhotoUrl(), entry.currentStreak(),
                    entry.totalSharedChallenges(), entry.completedToday(),
                    entry.halfCompletedToday(), challenges);
        }).toList();

        return ResponseEntity.ok(resp);
    }

    private UUID userId(Jwt jwt) {
        AuthenticatedUser auth = AuthenticatedUser.from(jwt);
        return userService.provisionUser(auth.keycloakId(), auth.email(), auth.preferredUsername()).getId();
    }
}
