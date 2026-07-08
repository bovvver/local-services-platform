package com.github.bovvver.experiencemanagement;

import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Read-side (query) repository for {@link ExperienceSnapshot} queries.
 * Operates directly on {@link ExperienceSnapshotEntity} — Spring Data provides the implementation.
 */
public interface ExperienceSnapshotReadRepository extends Repository<ExperienceSnapshotEntity, UUID> {

    Optional<ExperienceSnapshotEntity> findByUserId(UUID userId);
}
