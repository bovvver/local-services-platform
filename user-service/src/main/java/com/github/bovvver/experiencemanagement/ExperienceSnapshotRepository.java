package com.github.bovvver.experiencemanagement;

/**
 * Write-side (command) repository for the {@link ExperienceSnapshot} aggregate.
 * Contains only state-mutating operations.
 */
public interface ExperienceSnapshotRepository {
    ExperienceSnapshot save(ExperienceSnapshot snapshot);
}
