package com.github.bovvver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class User {

    private UserId id;
    private Email email;
    private String firstName;
    private String lastName;
    private Location location;
    private ExperienceLevel experienceLevel;
    private Set<ServiceCategory> serviceCategories;
    private Set<AwardTag> awardTags;
    private UserStatus status;

    private final List<OfferId> myOfferIds = new ArrayList<>();
    private final List<OfferId> assignedOfferIds = new ArrayList<>();

    private final List<BookingId> sentBookingIds = new ArrayList<>();
    private final List<BookingId> receivedBookingIds = new ArrayList<>();
}
