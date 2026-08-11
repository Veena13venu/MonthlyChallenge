package com.monthlychallenge.application.dto;

import com.monthlychallenge.domain.enums.ChallengeCategory;
import com.monthlychallenge.domain.enums.ChallengeFrequency;

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
