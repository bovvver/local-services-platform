package com.github.bovvver.profilemanagement;

import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Read-side (query) repository for {@link ProviderProfile} queries.
 * Operates directly on {@link ProviderProfileEntity} — Spring Data provides the implementation.
 */
public interface ProviderProfileReadRepository extends Repository<ProviderProfileEntity, UUID> {

    Optional<ProviderProfileEntity> findByUserId(UUID userId);
}
