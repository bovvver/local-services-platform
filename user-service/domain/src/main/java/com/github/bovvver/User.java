package com.github.bovvver;

import com.github.bovvver.vo.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class User {

    private final UserId id;
    private final Email email;
    private final String firstName;
    private final String lastName;
    private final Location location;
    private final ExperienceLevel experienceLevel;
    private final Set<ServiceCategory> serviceCategories;
    private final Set<AwardTag> awardTags;
    private final UserStatus status;

    private final List<OfferId> myOfferIds;
    private final List<OfferId> assignedOfferIds;
    private final List<BookingId> sentBookingIds;
    private final List<BookingId> receivedBookingIds;

    User(UserId id,
         Email email,
         String firstName,
         String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = null;

        this.experienceLevel = ExperienceLevel.BEGINNER;
        this.serviceCategories = new HashSet<>();
        this.awardTags = new HashSet<>();
        this.status = UserStatus.UNVERIFIED;

        this.myOfferIds = new ArrayList<>();
        this.assignedOfferIds = new ArrayList<>();
        this.sentBookingIds = new ArrayList<>();
        this.receivedBookingIds = new ArrayList<>();
    }

    static User create(
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

    Location getLocation() {
        return location;
    }
}