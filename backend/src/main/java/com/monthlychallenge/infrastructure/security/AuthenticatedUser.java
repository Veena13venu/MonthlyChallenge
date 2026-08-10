package com.monthlychallenge.infrastructure.security;

import org.springframework.security.oauth2.jwt.Jwt;

public record AuthenticatedUser(String keycloakId, String email, String preferredUsername) {

    public static AuthenticatedUser from(Jwt jwt) {

        System.out.println(jwt.getClaims());

        return new AuthenticatedUser(
                jwt.getSubject(),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("preferred_username")
        );
    }
}
