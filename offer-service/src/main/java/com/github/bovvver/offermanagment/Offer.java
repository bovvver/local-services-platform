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

    Offer(final OfferId id,
          final Title title,
          final Description description,
          final UserId authorId,
          final UserId executorId,
          final Set<BookingId> bookingIds,
          final Location location,
          final Set<ServiceCategory> serviceCategories,
          final Salary salary,
          final OfferStatus status,
          final LocalDateTime createdAt,
          final LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.executorId = executorId;
        this.bookingIds = bookingIds;
        this.location = location;
        this.serviceCategories = serviceCategories;
        this.salary = salary;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

        this(id, title, description, authorId, null,
                new HashSet<>(), location, serviceCategories, salary,
                OfferStatus.OPEN, LocalDateTime.now(), LocalDateTime.now());
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

    OfferId getId() {
        return id;
    }

    Title getTitle() {
        return title;
    }

    Description getDescription() {
        return description;
    }

    UserId getAuthorId() {
        return authorId;
    }

    UserId getExecutorId() {
        return executorId;
    }

    Set<BookingId> getBookingIds() {
        return bookingIds;
    }

    Location getLocation() {
        return location;
    }

    Set<ServiceCategory> getServiceCategories() {
        return serviceCategories;
    }

    Salary getSalary() {
        return salary;
    }

    OfferStatus getStatus() {
        return status;
    }

    LocalDateTime getCreatedAt() {
        return createdAt;
    }

    LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
