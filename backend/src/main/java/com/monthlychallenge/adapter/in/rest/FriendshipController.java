package com.monthlychallenge.adapter.in.rest;

import com.monthlychallenge.adapter.in.rest.dto.request.SendFriendRequestRequest;
import com.monthlychallenge.adapter.out.persistence.friendship.FriendshipJpaEntity;
import com.monthlychallenge.application.dto.FriendshipResponse;
import com.monthlychallenge.application.usecase.FriendshipService;
import com.monthlychallenge.application.usecase.UserService;
import com.monthlychallenge.domain.enums.FriendshipStatus;
import com.monthlychallenge.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/friendships")
@Tag(name = "Friendships", description = "Friend request system (FR-23 to FR-27)")
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final UserService userService;

    public FriendshipController(FriendshipService friendshipService, UserService userService) {
        this.friendshipService = friendshipService;
        this.userService = userService;
    }

    @PostMapping("/requests")
    @Operation(summary = "Send a friend request (FR-23)")
    public ResponseEntity<FriendshipResponse> sendRequest(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SendFriendRequestRequest req) {
        FriendshipJpaEntity f = friendshipService.sendFriendRequest(userId(jwt), req.getAddresseeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(f));
    }

    @PostMapping("/requests/{id}/accept")
    @Operation(summary = "Accept a friend request (FR-24, FR-25)")
    public ResponseEntity<FriendshipResponse> accept(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        FriendshipJpaEntity f = friendshipService.acceptFriendRequest(userId(jwt), id);
        return ResponseEntity.ok(toResponse(f));
    }

    @PostMapping("/requests/{id}/decline")
    @Operation(summary = "Decline a friend request (FR-24)")
    public ResponseEntity<Void> decline(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        friendshipService.declineFriendRequest(userId(jwt), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a friend (FR-26)")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        friendshipService.removeFriend(userId(jwt), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get accepted friends list")
    public ResponseEntity<List<FriendshipResponse>> getFriends(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(friendshipService.getAcceptedFriends(userId(jwt))
                .stream().map(this::toResponse).toList());
    }

    @GetMapping("/requests/pending")
    @Operation(summary = "Get pending friend requests (FR-27)")
    public ResponseEntity<List<FriendshipResponse>> getPending(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(friendshipService.getPendingRequests(userId(jwt))
                .stream().map(this::toResponse).toList());
    }

    private UUID userId(Jwt jwt) {
        AuthenticatedUser auth = AuthenticatedUser.from(jwt);
        return userService.provisionUser(auth.keycloakId(), auth.email(), auth.preferredUsername()).getId();
    }

    private FriendshipResponse toResponse(FriendshipJpaEntity f) {
        return new FriendshipResponse(f.getId(), f.getRequesterId(),
                f.getAddresseeId(), FriendshipStatus.valueOf(f.getStatus()), f.getCreatedAt());
    }
}
