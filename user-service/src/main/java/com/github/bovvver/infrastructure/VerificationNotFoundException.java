package com.github.bovvver.infrastructure;

import java.util.UUID;

public class VerificationNotFoundException extends RuntimeException {
    public VerificationNotFoundException(UUID userId) {
        super("Verification record not found for user: " + userId);
    }
}
