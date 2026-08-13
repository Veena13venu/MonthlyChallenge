package com.monthlychallenge.application.ports.in.command;

public record UpdateMinimumTargetCommand(
        double value,
        boolean isPercentage
) {}
