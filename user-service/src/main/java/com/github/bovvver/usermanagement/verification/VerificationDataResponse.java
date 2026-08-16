package com.github.bovvver.usermanagement.verification;

import java.time.LocalDateTime;
import java.util.UUID;

public record VerificationDataResponse(
        UUID userId,
        String message,
        LocalDateTime updatedAt
) {
    VerificationDataResponse(final UUID userId, final String message) {
        this(userId, message, LocalDateTime.now());
    }

    public static VerificationDataResponse of(final UUID userId) {
        return new VerificationDataResponse(userId, "Verification data uploaded successfully.");
    }
}
