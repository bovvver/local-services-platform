package com.github.bovvver.reputationmanagement;

/**
 * Write-side (command) repository for the {@link Reputation} aggregate.
 * Contains only state-mutating operations.
 */
public interface ReputationRepository {
    Reputation save(Reputation reputation);
}
