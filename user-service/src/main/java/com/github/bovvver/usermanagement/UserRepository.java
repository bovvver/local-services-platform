package com.github.bovvver.usermanagement;

import java.util.Optional;

/**
 * Write-side (command) repository for the {@link User} aggregate.
 * Contains state-mutating operations and loading by identity.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(com.github.bovvver.vo.UserId id);
}
