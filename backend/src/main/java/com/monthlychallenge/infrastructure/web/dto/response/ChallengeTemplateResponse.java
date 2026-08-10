package com.monthlychallenge.infrastructure.web.dto.response;

import com.monthlychallenge.domain.model.ChallengeCategory;
import com.monthlychallenge.domain.model.ChallengeFrequency;

import java.util.UUID;

public record ChallengeTemplateResponse(
        UUID id,
        String title,
        String description,
        ChallengeCategory category,
        ChallengeFrequency suggestedFrequency,
        Double suggestedTargetValue,
        String suggestedTargetUnit
) {}
