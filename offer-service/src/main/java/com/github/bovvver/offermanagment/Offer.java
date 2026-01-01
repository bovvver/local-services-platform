package com.github.bovvver.offermanagment;

import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.offermanagment.vo.*;

import java.time.LocalDateTime;
import java.util.*;

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
    private UserId executorId;
    private final Set<BookingId> bookingIds;
    private final Location location;
    private final Set<ServiceCategory> serviceCategories;
    private final Salary salary;
    private OfferStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DomainEvent> domainEvents;

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
        this.bookingIds = new HashSet<>(bookingIds);
        this.location = location;
        this.serviceCategories = new HashSet<>(serviceCategories);
        this.salary = salary;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.domainEvents = new ArrayList<>();
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
        if (authorId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new Offer(new OfferId(UUID.randomUUID()), title, description, authorId, location, serviceCategories, salary);
    }

    public void book(
            UserId userId,
            BookingId bookingId
    ) {
        if (isClosedForBooking()) {
            registerEvent(new BookingDraftRejected(this.id, userId, bookingId));
        }
        this.bookingIds.add(bookingId);
        this.updatedAt = LocalDateTime.now();
        registerEvent(new BookingDraftAccepted(this.id, userId, bookingId));
    }

    public void negotiate() {
        if (isClosedForBooking()) {
            throw new IllegalStateException("Offer %s is not open for booking.".formatted(id.value()));
        }
        updateStatus(OfferStatus.IN_NEGOTIATION);
    }

    public void accept(UserId executorId) {
        if (isClosedForBooking()) {
            throw new IllegalStateException("Offer %s is not open for booking.".formatted(id.value()));
        }
        updateStatus(OfferStatus.ASSIGNED);
        this.executorId = executorId;
    }

    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = getDomainEvents();
        domainEvents.clear();
        return events;
    }

    private boolean isClosedForBooking() {
        return !Arrays.asList(OfferStatus.OPEN, OfferStatus.IN_NEGOTIATION).contains(status);
    }

    private void updateStatus(OfferStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
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

    public UserId getExecutorId() {
        return executorId;
    }

    public Set<BookingId> getBookingIds() {
        return bookingIds;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }
}
