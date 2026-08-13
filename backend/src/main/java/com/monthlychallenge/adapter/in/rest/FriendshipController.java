package com.monthlychallenge.adapter.in.rest;

import com.monthlychallenge.adapter.in.rest.dto.request.SendFriendRequestRequest;
import com.monthlychallenge.adapter.in.rest.mapper.FriendshipWebMapper;
import com.monthlychallenge.application.dto.FriendshipResponse;
import com.monthlychallenge.application.ports.in.FriendshipUseCase;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/friendships")
@Tag(name = "Friendships", description = "Friend request system (FR-23 to FR-27)")
public class FriendshipController {

    private final FriendshipUseCase friendshipUseCase;
    private final UserUseCase userUseCase;
    private final FriendshipWebMapper mapper;

    public FriendshipController(FriendshipUseCase friendshipUseCase,
                                 UserUseCase userUseCase,
                                 FriendshipWebMapper mapper) {
        this.friendshipUseCase = friendshipUseCase;
        this.userUseCase = userUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/requests")
    @Operation(summary = "Send a friend request (FR-23)")
    public ResponseEntity<FriendshipResponse> sendRequest(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SendFriendRequestRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(friendshipUseCase.sendFriendRequest(userId(jwt), req.getAddresseeId())));
    }

    @PostMapping("/requests/{id}/accept")
    @Operation(summary = "Accept a friend request (FR-24, FR-25)")
    public ResponseEntity<FriendshipResponse> accept(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(friendshipUseCase.acceptFriendRequest(userId(jwt), id)));
    }

    @PostMapping("/requests/{id}/decline")
    @Operation(summary = "Decline a friend request (FR-24)")
    public ResponseEntity<Void> decline(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        friendshipUseCase.declineFriendRequest(userId(jwt), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a friend (FR-26)")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        friendshipUseCase.removeFriend(userId(jwt), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get accepted friends list")
    public ResponseEntity<List<FriendshipResponse>> getFriends(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(friendshipUseCase.getAcceptedFriends(userId(jwt))
                .stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/requests/pending")
    @Operation(summary = "Get pending friend requests (FR-27)")
    public ResponseEntity<List<FriendshipResponse>> getPending(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(friendshipUseCase.getPendingRequests(userId(jwt))
                .stream().map(mapper::toResponse).toList());
    }

    private UUID userId(Jwt jwt) {
        AuthenticatedUser auth = AuthenticatedUser.from(jwt);
        return userUseCase.provisionUserFromKeycloak(
                auth.keycloakId(), auth.email(), auth.preferredUsername()).getId();
    }
}
