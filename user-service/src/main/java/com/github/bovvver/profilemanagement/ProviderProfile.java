package com.github.bovvver.profilemanagement;

import com.github.bovvver.vo.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * ProviderProfile aggregate — business profile of a service provider.
 *
 * <p>Always references a {@link UserId} but never holds a direct reference to the
 * {@code User} aggregate. Exactly one profile exists per user and is created
 * automatically after the {@code UserCreated} event is processed.</p>
 */
public class ProviderProfile {

    private final ProviderProfileId id;
    private final UserId userId;
    private String bio;
    private City city;
    private Country country;
    private final Set<ServiceCategory> categories;

    ProviderProfile(final ProviderProfileId id,
                    final UserId userId,
                    final String bio,
                    final City city,
                    final Country country,
                    final Set<ServiceCategory> categories) {
        this.id = id;
        this.userId = userId;
        this.bio = bio;
        this.city = city;
        this.country = country;
        this.categories = new HashSet<>(categories);
    }

    /**
     * Factory — creates a blank profile for the given user.
     * Called when a {@code UserCreated} event is consumed.
     *
     * @param userId the owning user's identifier
     * @return a newly created, empty {@code ProviderProfile}
     */
    public static ProviderProfile createFor(UserId userId) {
        return new ProviderProfile(
                ProviderProfileId.generate(),
                userId,
                null,
                null,
                null,
                new HashSet<>()
        );
    }

    public ProviderProfileId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getBio() {
        return bio;
    }

    public City getCity() {
        return city;
    }

    public Country getCountry() {
        return country;
    }

    public Set<ServiceCategory> getCategories() {
        return Collections.unmodifiableSet(categories);
    }
}
