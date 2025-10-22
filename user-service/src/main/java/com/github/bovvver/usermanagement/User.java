package com.github.bovvver.usermanagement;

import com.github.bovvver.usermanagement.vo.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a user in the system.
 * A user is identified by a unique {@link UserId} and has an associated {@link Email},
 * personal details (first name, last name), city, country, experience level,
 * service categories, award tags, and user status.
 *
 * <p>Each user can own offers, be assigned to offers,
 * and send booking requests.</p>
 */
public class User {

    private final UserId id;
    private final Email email;
    private final String firstName;
    private final String lastName;
    private final City city;
    private final Country country;
    private final ExperienceLevel experienceLevel;
    private final Set<ServiceCategory> serviceCategories;
    private final Set<AwardTag> awardTags;
    private final UserStatus status;

    private final List<OfferId> myOfferIds;
    private final List<OfferId> assignedOfferIds;
    private final List<BookingId> sentBookingIds;

    User(final UserId id,
         final Email email,
         final String firstName,
         final String lastName,
         final City city,
         final Country country,
         final ExperienceLevel experienceLevel,
         final Set<ServiceCategory> serviceCategories,
         final Set<AwardTag> awardTags,
         final UserStatus status,
         final List<OfferId> myOfferIds,
         final List<OfferId> assignedOfferIds,
         final List<BookingId> sentBookingIds) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.country = country;
        this.experienceLevel = experienceLevel;
        this.serviceCategories = serviceCategories;
        this.awardTags = awardTags;
        this.status = status;
        this.myOfferIds = myOfferIds;
        this.assignedOfferIds = assignedOfferIds;
        this.sentBookingIds = sentBookingIds;
    }

    /**
     * Creates a new user with minimal required information.
     * <p>Default values:</p>
     * <ul>
     *     <li>{@link City} = {@code null}</li>
     *     <li>{@link Country} = {@code null}</li>
     *     <li>{@link ExperienceLevel} = {@link ExperienceLevel#BEGINNER}</li>
     *     <li>{@link UserStatus} = {@link UserStatus#UNVERIFIED}</li>
     *     <li>{@link #serviceCategories}, {@link #awardTags}, {@link #myOfferIds},
     *         {@link #assignedOfferIds}, {@link #sentBookingIds} are initialized as empty collections</li>
     * </ul>
     *
     * @param id        unique user identifier
     * @param email     email address
     * @param firstName first name
     * @param lastName  last name
     */
    User(UserId id,
         Email email,
         String firstName,
         String lastName) {
        this(id, email, firstName, lastName, null, null,
                ExperienceLevel.BEGINNER, new HashSet<>(), new HashSet<>(),
                UserStatus.UNVERIFIED, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Factory method for creating a new {@code User} instance.
     *
     * @param id        unique user identifier
     * @param email     email address
     * @param firstName first name
     * @param lastName  last name
     * @return newly created {@code User}
     */
    public static User create(
            UserId id,
            Email email,
            String firstName,
            String lastName
    ) {
        return new User(id, email, firstName, lastName);
    }

    UserId getId() {
        return id;
    }

    Email getEmail() {
        return email;
    }

    String getFirstName() {
        return firstName;
    }

    String getLastName() {
        return lastName;
    }

    City getCity() {
        return city;
    }

    Country getCountry() {
        return country;
    }

    ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    Set<ServiceCategory> getServiceCategories() {
        return serviceCategories;
    }

    Set<AwardTag> getAwardTags() {
        return awardTags;
    }

    UserStatus getStatus() {
        return status;
    }

    List<OfferId> getMyOfferIds() {
        return myOfferIds;
    }

    List<OfferId> getAssignedOfferIds() {
        return assignedOfferIds;
    }

    List<BookingId> getSentBookingIds() {
        return sentBookingIds;
    }
}