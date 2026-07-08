package com.github.bovvver.reputationmanagement;

import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Read-side (query) repository for {@link Reputation} queries.
 * Operates directly on {@link ReputationEntity} — Spring Data provides the implementation.
 */
public interface ReputationReadRepository extends Repository<ReputationEntity, UUID> {

    Optional<ReputationEntity> findByUserId(UUID userId);
}
