package com.github.bovvver.usermanagement.keycloakusercreation;

import com.github.bovvver.profilemanagement.ProviderProfile;
import com.github.bovvver.profilemanagement.ProviderProfileRepository;
import com.github.bovvver.reputationmanagement.Reputation;
import com.github.bovvver.reputationmanagement.ReputationRepository;
import com.github.bovvver.usermanagement.User;
import com.github.bovvver.usermanagement.UserRepository;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service facade for user-related operations.
 *
 * <p>Creates a new user atomically: {@link User}, {@link com.github.bovvver.profilemanagement.ProviderProfile},
 * and {@link Reputation} are all persisted in the same transaction. Any failure
 * rolls back the entire registration — a user without a profile or reputation row
 * is in an invalid state.</p>
 */
@Service
@RequiredArgsConstructor
public class UserManagementFacade {

    private final UserRepository userRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final ReputationRepository reputationRepository;

    /**
     * Creates a new user from Keycloak registration data.
     *
     * <p>Atomically persists the {@link User}, a blank {@link ProviderProfile},
     * and a zeroed {@link Reputation} inside a single transaction.</p>
     *
     * @param command the command carrying Keycloak user data
     * @return the persisted {@link User} aggregate
     */
    @Transactional
    User createUserFromKeycloak(CreateUserCommand command) {
        User user = User.create(
                UserId.from(command.userId()),
                new Email(command.email()),
                command.firstName(),
                command.lastName()
        );

        User savedUser = userRepository.save(user);

        // Core invariants — must succeed atomically with user creation
        providerProfileRepository.save(ProviderProfile.createFor(savedUser.getId()));
        reputationRepository.save(Reputation.initialize(savedUser.getId()));
        return savedUser;
    }
}
