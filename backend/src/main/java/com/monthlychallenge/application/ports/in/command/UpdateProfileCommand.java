package com.monthlychallenge.application.ports.in.command;

public record UpdateProfileCommand(
        String displayName,
        String profilePhotoUrl
) {}
