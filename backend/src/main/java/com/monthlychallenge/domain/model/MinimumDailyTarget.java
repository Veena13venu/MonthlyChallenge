package com.monthlychallenge.domain.model;

public final class MinimumDailyTarget {

    private final double value;
    private final boolean isPercentage;

    public MinimumDailyTarget(double value, boolean isPercentage) {
        this.value = value;
        this.isPercentage = isPercentage;
    }

    public double getValue() { return value; }
    public boolean isPercentage() { return isPercentage; }

    public double resolveThreshold(int totalDueChallenges) {
        if (isPercentage) {
            return (value / 100.0) * totalDueChallenges;
        }
        return value;
    }
}
