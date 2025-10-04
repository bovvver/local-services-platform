package com.github.bovvver.offermanagment;

import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.offermanagment.vo.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Represents an offer in the system.
 * <p>
 * An offer is created by a user (author) and may be assigned to an executor.
 * It contains details such as title, description, location, service categories,
 * salary, status, and associated bookings.
 * </p>
 */
public class Offer {

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
     * @param title             title of the offer
     * @param description       detailed description
     * @param authorId          identifier of the user who created the offer
     * @param location          location of the offer
     * @param serviceCategories categories of services
     * @param salary            salary or payment information
     * @return newly created {@code Offer}
     */
    public static Offer create(
            Title title,
            Description description,
            UserId authorId,
            Location location,
            Set<ServiceCategory> serviceCategories,
            Salary salary
    ) {
        return new Offer(new OfferId(UUID.randomUUID()), title, description, authorId, location, serviceCategories, salary);
    }

    public DomainEvent book(
            UserId userId,
            BookingId bookingId
    ) {
        if (!List.of(OfferStatus.OPEN, OfferStatus.IN_NEGOTIATION).contains(this.status)) {
            return new BookingDraftRejected(this.id, userId, bookingId);
        }
        this.bookingIds.add(bookingId);
        return new BookingDraftAccepted(this.id, userId, bookingId);
    }

    public OfferId getId() {
        return id;
    }

    public Title getTitle() {
        return title;
    }

    public Description getDescription() {
        return description;
    }

    public UserId getAuthorId() {
        return authorId;
    }

    public Location getLocation() {
        return location;
    }

    public Set<ServiceCategory> getServiceCategories() {
        return serviceCategories;
    }

    public Salary getSalary() {
        return salary;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
