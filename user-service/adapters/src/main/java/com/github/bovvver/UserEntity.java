package com.github.bovvver;

import com.github.bovvver.vo.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
class UserEntity {

    @Getter
    @Id
    private UUID id;

    @Getter
    @Column(nullable = false, unique = true)
    private String email;

    @Getter
    @Column(nullable = false)
    private String firstName;

    @Getter
    @Column(nullable = false)
    private String lastName;

    @Getter
    @Embedded
    private LocationEmbeddable location;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_service_categories", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Set<ServiceCategory> serviceCategories = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_award_tags", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private Set<AwardTag> awardTags = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_my_offers", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "offer_id")
    private List<UUID> myOfferIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_assigned_offers", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "offer_id")
    private List<UUID> assignedOfferIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_sent_bookings", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "booking_id")
    private List<UUID> sentBookingIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_received_bookings", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "booking_id")
    private List<UUID> receivedBookingIds = new ArrayList<>();

    UserEntity(UUID id,
               String email,
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

}
