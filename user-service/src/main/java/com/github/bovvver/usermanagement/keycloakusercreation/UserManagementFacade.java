package com.github.bovvver.usermanagement.keycloakusercreation;

import com.github.bovvver.event.DomainEventPublisher;
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
 * <p>On creation, persists the {@link User} aggregate then publishes its
 * domain events via {@link com.github.bovvver.event.DomainEventPublisher}.
 * Each aggregate's {@code UserCreatedListener} reacts via
 * {@link org.springframework.transaction.event.TransactionalEventListener} after commit,
 * initialising ProviderProfile, Reputation, Verification, and ExperienceSnapshot.</p>
 */
@Service
@RequiredArgsConstructor
public class UserManagementFacade {

    private final UserRepository userRepository;
    private final DomainEventPublisher domainEventPublisher;

    /**
     * Creates a new user from Keycloak registration data and publishes
     * a {@link com.github.bovvver.usermanagement.keycloakusercreation.UserCreated} domain event
     * to trigger initialisation of downstream aggregates.
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
        domainEventPublisher.publish(user.pullDomainEvents());
        return savedUser;
    }
}
