package com.monthlychallenge.domain.model;

/**
 * Supported categories for a challenge.
 * Maps to a database column value — keep values stable across releases.
 */
public enum ChallengeCategory {
    HEALTH,
    FITNESS,
    LEARNING,
    MINDFULNESS,
    NUTRITION,
    SLEEP,
    PRODUCTIVITY,
    OTHER
}
