package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.bookingcreation.BookingCreated;
import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.infrastructure.*;
import com.github.bovvver.bookingmanagement.negotiation.NegotiationStarted;
import com.github.bovvver.bookingmanagement.resolvebookingdecision.BookingAccepted;
import com.github.bovvver.bookingmanagement.vo.*;

import java.time.LocalDateTime;
import java.util.*;

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
    private Salary salary;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;
    private final List<DomainEvent> domainEvents;

    Booking(final BookingId id,
            final UserId userId,
            final OfferId offerId,
            final Negotiation negotiation,
            final BookingStatus status,
            final Salary salary,
            final LocalDateTime createdAt,
            final LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.offerId = offerId;
        this.negotiation = negotiation;
        this.status = status;
        this.salary = salary;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
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
     * @param id      unique identifier of the booking
     * @param userId  identifier of the user making the booking
     * @param offerId identifier of the offer being booked
     * @param salary  final salary for the booking, if any
     */
    Booking(BookingId id,
            UserId userId,
            OfferId offerId,
            Salary salary
    ) {
        this(id, userId, offerId, null, BookingStatus.PENDING, salary, LocalDateTime.now(), LocalDateTime.now().plusDays(14));
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

    public void beginNegotiation(Salary proposedSalary, UserId offerAuthorId) {
        validateStatus(BookingStatus.PENDING);
        updateStatus(BookingStatus.IN_NEGOTIATION);

        Negotiation negotiation = Negotiation.create(this.id, offerAuthorId);
        negotiation.addPosition(proposedSalary, NegotiationParty.AUTHOR);

        this.negotiation = negotiation;
        registerEvent(new NegotiationStarted(this.getOfferId(), this.getId()));
    }

    public void addPositionToNegotiation(Salary proposedSalary, UserId proposedBy) {
        NegotiationParty negotiationParty = negotiationPartyFor(proposedBy);

        validateStatus(BookingStatus.IN_NEGOTIATION);
        this.negotiation.addPosition(proposedSalary, negotiationParty);
    }

    public void acceptNegotiationProposal(UserId acceptedBy, NegotiationPositionId positionId) {
        NegotiationPosition position = validatedLatestPositionProposedByOtherParty(acceptedBy, positionId);
        this.salary = position.getProposedSalary();
        this.negotiation.acceptNegotiation();
        updateStatus(BookingStatus.ACCEPTED);
        registerEvent(new BookingAccepted(this.getOfferId(), this.getUserId(), this.getId()));
    }

    public void rejectNegotiationProposal(UserId acceptedBy, NegotiationPositionId positionId) {
        validatedLatestPositionProposedByOtherParty(acceptedBy, positionId);
        this.negotiation.rejectNegotiation();
        updateStatus(BookingStatus.PENDING);
    }

    private NegotiationPosition validatedLatestPositionProposedByOtherParty(
            UserId decidedBy,
            NegotiationPositionId positionId
    ) {
        NegotiationParty negotiationParty = negotiationPartyFor(decidedBy);
        List<NegotiationPosition> positions = negotiation.getPositions();

        NegotiationPosition position = positions.stream()
                .filter(p -> p.getId().equals(positionId))
                .findFirst()
                .orElseThrow(() -> new PositionNotFoundException(positionId.value()));

        NegotiationPosition latestPosition = positions.stream()
                .max(Comparator.comparing(NegotiationPosition::getProposedAt))
                .orElseThrow(() -> new PositionNotFoundException(positionId.value()));

        if (!latestPosition.getId().equals(positionId)) {
            throw new OutdatedNegotiationPositionException(positionId.value());
        }
        if (negotiationParty == position.getProposedBy()) {
            throw new OwnNegotiationProposalDecisionException();
        }
        return position;
    }

    public void cancelNegotiation() {
        validateStatus(BookingStatus.IN_NEGOTIATION);
        updateStatus(BookingStatus.PENDING);
        this.negotiation = null;
    }

    public void cancelByAuthor() {
        cancel(BookingStatus.CANCELED_BY_AUTHOR);
    }

    public void cancelByExecutor() {
        cancel(BookingStatus.CANCELED_BY_EXECUTOR);
    }

    public void accept() {
        validateStatus(BookingStatus.PENDING);
        updateStatus(BookingStatus.ACCEPTED);
        registerEvent(new BookingAccepted(this.getOfferId(), this.getUserId(), this.getId()));
    }

    public void reject() {
        validateStatus(BookingStatus.PENDING, BookingStatus.IN_NEGOTIATION);
        updateStatus(BookingStatus.REJECTED);
    }

    public void expire(LocalDateTime now) {
        validateStatus(BookingStatus.PENDING, BookingStatus.IN_NEGOTIATION);

        if (now.isBefore(expiresAt)) {
            throw new BookingNotExpiredYetException(id.value());
        }
        updateStatus(BookingStatus.EXPIRED);
    }

    /**
     * Returns negotiation party (AUTHOR/EXECUTOR) for a given user.
     * <p>
     * Domain invariant: this method can be used only when booking is in negotiation.
     * </p>
     */
    private NegotiationParty negotiationPartyFor(UserId currentUserId) {
        validateStatus(BookingStatus.IN_NEGOTIATION);

        if (this.userId.equals(currentUserId)) {
            return NegotiationParty.EXECUTOR;
        }
        if (this.negotiation.getOfferAuthorId().equals(currentUserId)) {
            return NegotiationParty.AUTHOR;
        }
        throw new BookingOwnershipException("Current user is not a party of the negotiation");
    }

    private void validateStatus(BookingStatus... allowedStatuses) {
        if (!Arrays.asList(allowedStatuses).contains(this.status)) {
            throw new OperationNotAllowedInCurrentStateException(this.status);
        }
    }

    private void updateStatus(BookingStatus status) {
        this.status = status;
    }

    private void cancel(BookingStatus cancelStatus) {
        validateStatus(BookingStatus.PENDING, BookingStatus.IN_NEGOTIATION, BookingStatus.ACCEPTED);
        updateStatus(cancelStatus);
        this.negotiation = null;
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

    public Negotiation getNegotiation() {
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

    LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }
}