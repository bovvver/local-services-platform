package com.github.bovvver.profilemanagement;

/**
 * Write-side (command) repository for the {@link ProviderProfile} aggregate.
 * Contains only state-mutating operations.
 */
public interface ProviderProfileRepository {
    ProviderProfile save(ProviderProfile profile);
}
