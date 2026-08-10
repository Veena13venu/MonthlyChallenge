package com.monthlychallenge.infrastructure.web.dto.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String displayName,
        String email,
        String profilePhotoUrl,
        double minimumTargetValue,
        boolean minimumTargetIsPercentage
) {}
