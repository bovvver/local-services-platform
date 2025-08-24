package com.github.bovvver;

import com.github.bovvver.vo.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an offer in the system.
 * <p>
 * An offer is created by a user (author) and may be assigned to an executor.
 * It contains details such as title, description, location, service categories,
 * salary, status, and associated bookings.
 * </p>
 */
class Offer {

    private final OfferId id;
    private final Title title;
    private final Description description;
    private final UserId authorId;
    private final UserId executorId;
    private final Set<BookingId> bookingIds;
    private final Location location;
    private final Set<ServiceCategory> serviceCategories;
    private final Salary salary;
    private final OfferStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /**
     * Constructs a new offer with the required fields.
     * <p>Default values:</p>
     * <ul>
     *     <li>{@link #executorId} = {@code null}</li>
     *     <li>{@link #bookingIds} initialized as empty set</li>
     *     <li>{@link #status} = {@link OfferStatus#OPEN}</li>
     *     <li>{@link #createdAt} and {@link #updatedAt} = current date/time</li>
     * </ul>
     *
     * @param id                unique identifier of the offer
     * @param title             title of the offer
     * @param description       detailed description
     * @param authorId          identifier of the user who created the offer
     * @param location          location of the offer
     * @param serviceCategories categories of services
     * @param salary            salary or payment information
     */
    Offer(OfferId id,
          Title title,
          Description description,
          UserId authorId,
          Location location,
          Set<ServiceCategory> serviceCategories,
          Salary salary) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.executorId = null;
        this.bookingIds = new HashSet<>();
        this.location = location;
        this.serviceCategories = serviceCategories;
        this.salary = salary;
        this.status = OfferStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Factory method for creating a new {@code Offer} instance.
     *
     * @param id                unique identifier of the offer
     * @param title             title of the offer
     * @param description       detailed description
     * @param authorId          identifier of the user who created the offer
     * @param location          location of the offer
     * @param serviceCategories categories of services
     * @param salary            salary or payment information
     * @return newly created {@code Offer}
     */
    static Offer create(
            OfferId id,
            Title title,
            Description description,
            UserId authorId,
            Location location,
            Set<ServiceCategory> serviceCategories,
            Salary salary
    ) {
        return new Offer(id, title, description, authorId, location, serviceCategories, salary);
    }
}
