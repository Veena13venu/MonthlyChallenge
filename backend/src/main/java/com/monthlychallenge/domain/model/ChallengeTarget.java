package com.monthlychallenge.domain.model;

public final class ChallengeTarget {

    private final double value;
    private final String unit;

    public ChallengeTarget(double value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() { return value; }
    public String getUnit()  { return unit; }
}
