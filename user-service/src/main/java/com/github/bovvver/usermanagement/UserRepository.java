package com.github.bovvver.usermanagement;

/**
 * Write-side (command) repository for the {@link User} aggregate.
 * Contains only state-mutating operations.
 */
public interface UserRepository {
    User save(User user);
}
