package com.monthlychallenge.domain.model;

/**
 * The overall outcome of a single calendar day.
 * Drives streak calculation and calendar view colour coding.
 *
 * SUCCESS  → green  (points ≥ minimum target)
 * PARTIAL  → amber  (0 < points < minimum target)
 * MISSED   → red    (0 points)
 */
public enum DayResult {
    SUCCESS,
    PARTIAL,
    MISSED
}
