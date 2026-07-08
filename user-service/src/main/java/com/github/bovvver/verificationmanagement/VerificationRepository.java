package com.github.bovvver.verificationmanagement;

/**
 * Write-side (command) repository for the {@link Verification} aggregate.
 * Contains only state-mutating operations.
 */
public interface VerificationRepository {
    Verification save(Verification verification);
}
