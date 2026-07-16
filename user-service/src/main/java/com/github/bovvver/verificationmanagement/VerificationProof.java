package com.github.bovvver.verificationmanagement;

import java.time.LocalDateTime;

public record VerificationProof(
        String url,
        LocalDateTime uploadedAt
) {
    public static VerificationProof of(String url) {
        return new VerificationProof(url, LocalDateTime.now());
    }
}
