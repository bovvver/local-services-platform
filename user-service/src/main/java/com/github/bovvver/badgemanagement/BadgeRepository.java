package com.github.bovvver.badgemanagement;

/**
 * Write-side (command) repository for the {@link Badge} aggregate.
 * Contains only state-mutating operations.
 */
public interface BadgeRepository {
    Badge save(Badge badge);
}
