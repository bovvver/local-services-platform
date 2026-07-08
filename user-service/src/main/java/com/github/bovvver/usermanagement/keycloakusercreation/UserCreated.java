package com.github.bovvver.usermanagement.keycloakusercreation;

import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.usermanagement.User;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;

import java.time.LocalDateTime;

/**
 * Domain event raised when a new {@link User} is successfully created.
 * Published via {@link com.github.bovvver.event.DomainEventPublisher} after the
 * user is persisted, triggering initialisation of downstream aggregates
 * (ProviderProfile, Reputation, Verification, ExperienceSnapshot) through their
 * respective {@link org.springframework.transaction.event.TransactionalEventListener}s.
 */
public record UserCreated(
        UserId userId,
        Email email,
        String firstName,
        String lastName,
        LocalDateTime timestamp
) implements DomainEvent {

    public UserCreated(UserId userId, Email email, String firstName, String lastName) {
        this(userId, email, firstName, lastName, LocalDateTime.now());
    }
}
