package com.github.bovvver.verificationmanagement;

import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Read-side (query) repository for {@link Verification} queries.
 * Operates directly on {@link VerificationEntity} — Spring Data provides the implementation.
 */
public interface VerificationReadRepository extends Repository<VerificationEntity, UUID> {

    Optional<VerificationEntity> findByUserId(UUID userId);
}
