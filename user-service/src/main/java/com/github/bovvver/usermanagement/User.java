package com.github.bovvver.usermanagement;

import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.usermanagement.keycloakusercreation.UserCreated;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;

import java.util.ArrayList;
import java.util.List;

/**
 * User aggregate — responsible for identity and account lifecycle only.
 *
 * <p>Fields such as City, Country, ExperienceLevel, ServiceCategories, AwardTags,
 * OfferIds, and BookingIds have been extracted to dedicated aggregates
 * (ProviderProfile, ExperienceSnapshot, Badge, etc.).</p>
 */
public class User {

    private final UserId id;
    private final Email email;
    private final String firstName;
    private final String lastName;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    User(final UserId id,
         final Email email,
         final String firstName,
         final String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Factory method — creates a new user with UNVERIFIED status and registers
     * a {@link UserCreated} domain event to trigger downstream aggregate initialization.
     *
     * @param id        unique identifier (from Keycloak)
     * @param email     validated email address
     * @param firstName first name
     * @param lastName  last name
     * @return newly constructed {@code User} with a pending domain event
     */
    public static User create(
            UserId id,
            Email email,
            String firstName,
            String lastName
    ) {
        User user = new User(id, email, firstName, lastName);
        user.domainEvents.add(new UserCreated(id, email, firstName, lastName));
        return user;
    }

    /**
     * Drains and returns all pending domain events. Called by the facade after
     * persisting the aggregate so events can be published via {@link com.github.bovvver.event.DomainEventPublisher}.
     *
     * @return snapshot of pending events; list is cleared after call
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public UserId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}