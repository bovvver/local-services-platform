package com.github.bovvver;

import com.github.bovvver.vo.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * JPA entity representing a user stored in the database.
 * <p>
 * Mapped to the {@code users} table and contains basic user information,
 * embedded location, service categories, award tags, status, and relations
 * to offers and bookings (represented as UUID collections).
 * </p>
 *
 * <p>Collections are mapped using {@link ElementCollection} to store simple values
 * such as enums and identifiers in dedicated tables.</p>
 */
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

    private String city;

    private String country;

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

    /**
     * Creates a new {@code UserEntity} with basic required fields.
     * <p>Default values:</p>
     * <ul>
     *     <li>city = {@code null}</li>
     *     <li>country = {@code null}</li>
     *     <li>{@link ExperienceLevel} = {@link ExperienceLevel#BEGINNER}</li>
     *     <li>{@link UserStatus} = {@link UserStatus#UNVERIFIED}</li>
     *     <li>All collections initialized as empty</li>
     * </ul>
     *
     * @param id        unique identifier of the user
     * @param email     email address of the user
     * @param firstName first name
     * @param lastName  last name
     */
    UserEntity(UUID id,
               String email,
               String firstName,
               String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = null;
        this.country = null;

        this.experienceLevel = ExperienceLevel.BEGINNER;
        this.serviceCategories = new HashSet<>();
        this.awardTags = new HashSet<>();
        this.status = UserStatus.UNVERIFIED;

        this.myOfferIds = new ArrayList<>();
        this.assignedOfferIds = new ArrayList<>();
        this.sentBookingIds = new ArrayList<>();
    }
}
