package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.*;

import java.time.LocalDateTime;

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
 *     <li>{@link #createdAt} and {@link #updatedAt} are initialized at creation time.</li>
 *     <li>Status and timestamps may change later in the booking flow.</li>
 * </ul>
 */
public class Booking {

    private final BookingId id;
    private final UserId userId;
    private final OfferId offerId;
    private final BookingStatus status;
    private final Salary proposedSalary;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /**
     * Constructs a new booking with default values.
     * <p>Default values:</p>
     * <ul>
     *     <li>{@link #status} = {@link BookingStatus#PENDING}</li>
     *     <li>{@link #createdAt} = current timestamp</li>
     *     <li>{@link #updatedAt} = same as {@link #createdAt}</li>
     * </ul>
     *
     * @param id      unique identifier of the booking
     * @param userId  identifier of the user making the booking
     * @param offerId identifier of the offer being booked
     * @param proposedSalary proposed salary for the booking, if any
     */
    Booking(BookingId id,
            UserId userId,
            OfferId offerId,
            Salary proposedSalary
    ) {
        this.id = id;
        this.userId = userId;
        this.offerId = offerId;
        this.status = BookingStatus.PENDING;
        this.proposedSalary = proposedSalary;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
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
        return new Booking(id, userId, offerId, proposedSalary);
    }

    BookingId getId() {
        return id;
    }

    UserId getUserId() {
        return userId;
    }

    OfferId getOfferId() {
        return offerId;
    }

    Salary getProposedSalary() {
        return proposedSalary;
    }
}