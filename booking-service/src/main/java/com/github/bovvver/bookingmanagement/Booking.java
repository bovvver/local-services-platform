package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.bookingcreation.BookingCreated;
import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.negotiation.NegotiationStarted;
import com.github.bovvver.bookingmanagement.resolvebookingdecision.BookingAccepted;
import com.github.bovvver.bookingmanagement.vo.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Represents a booking made by a user for a specific offer.
 * <p>
 * A booking links a {@link UserId} with an {@link OfferId} and
 * contains its own identifier, status, and timestamps.
 * </p>
 *
 * <p>Lifecycle:</p>
 * <ul>
 *     <li>New bookings are created with {@link BookingStatus#PENDING} status.</li>
 *     <li>{@link #createdAt} is initialized at creation time.</li>
 *     <li>Status and timestamps may change later in the booking flow.</li>
 * </ul>
 */
public class Booking {

    private final BookingId id;
    private final UserId userId;
    private final OfferId offerId;
    private Negotiation negotiation;
    private BookingStatus status;
    private final Salary salary;
    private final LocalDateTime createdAt;
    private final List<DomainEvent> domainEvents;

    Booking(final BookingId id,
            final UserId userId,
            final OfferId offerId,
            final Negotiation negotiation,
            final BookingStatus status,
            final Salary salary,
            final LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.offerId = offerId;
        this.negotiation = negotiation;
        this.status = status;
        this.salary = salary;
        this.createdAt = createdAt;
        this.domainEvents = new ArrayList<>();
    }

    /**
     * Constructs a new booking with default values.
     * <p>Default values:</p>
     * <ul>
     *     <li>{@link #status} = {@link BookingStatus#PENDING}</li>
     *     <li>{@link #createdAt} = current timestamp</li>
     * </ul>
     *
     * @param id          unique identifier of the booking
     * @param userId      identifier of the user making the booking
     * @param offerId     identifier of the offer being booked
     * @param salary final salary for the booking, if any
     */
    Booking(BookingId id,
            UserId userId,
            OfferId offerId,
            Salary salary
    ) {
        this(id, userId, offerId, null, BookingStatus.PENDING, salary, LocalDateTime.now());
    }

    /**
     * Factory method for creating a new {@code Booking}.
     *
     * @param id      unique identifier of the booking
     * @param userId  identifier of the user making the booking
     * @param offerId identifier of the offer being booked
     * @return newly created {@code Booking} with default state
     */
    public static Booking create(
            BookingId id,
            UserId userId,
            OfferId offerId,
            Salary proposedSalary
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        if (offerId == null) {
            throw new IllegalArgumentException("OfferId cannot be null");
        }
        Booking booking = new Booking(id, userId, offerId, proposedSalary);
        booking.registerEvent(new BookingCreated(booking.getId(), booking.getUserId(), booking.getOfferId(), booking.getSalary()));
        return booking;
    }

    public static Booking create(
            UserId userId,
            OfferId offerId,
            Salary proposedSalary
    ) {
        return create(BookingId.of(UUID.randomUUID()), userId, offerId, proposedSalary);
    }

    public void beginNegotiation(Salary proposedSalary) {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot begin negotiation for booking with status %s".formatted(this.status)
            );
        }
        updateStatus(BookingStatus.IN_NEGOTIATION);

        Negotiation negotiation = Negotiation.create(this.id);
        negotiation.addPosition(proposedSalary, NegotiationParty.AUTHOR);

        this.negotiation = negotiation;
        registerEvent(new NegotiationStarted(this.getId(), negotiation.getId()));
    }

    public void accept() {
        validateStatusForAction("accept", BookingStatus.PENDING, BookingStatus.IN_NEGOTIATION);
        updateStatus(BookingStatus.ACCEPTED);
        registerEvent(new BookingAccepted(this.getOfferId(), this.getUserId()));
    }

    public void reject() {
        validateStatusForAction("reject", BookingStatus.PENDING, BookingStatus.IN_NEGOTIATION);
        updateStatus(BookingStatus.REJECTED);
    }

    private void validateStatusForAction(String action, BookingStatus... validStatuses) {
        if (Stream.of(validStatuses).noneMatch(status -> status == this.status)) {
            throw new IllegalStateException(
                    "Cannot %s booking with status %s".formatted(action, this.status)
            );
        }
    }

    private void updateStatus(BookingStatus status) {
        this.status = status;
    }

    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = getDomainEvents();
        domainEvents.clear();
        return events;
    }

    public BookingId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public OfferId getOfferId() {
        return offerId;
    }

    Negotiation getNegotiation() {
        return negotiation;
    }

    public BookingStatus getStatus() {
        return status;
    }

    Salary getSalary() {
        return salary;
    }

    LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }
}