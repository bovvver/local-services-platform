package com.github.bovvver.offermanagment.vo;

import com.github.bovvver.event.DomainEvent;

import java.time.LocalDateTime;

public class BookingDraftRejected implements DomainEvent {

    private static final String EVENT_MESSAGE = "Booking request rejected. Offer cannot accept more bookings.";

    private final BookingRequestStatus status;
    private final String message;
    private final OfferId offerId;
    private final UserId userId;
    private final BookingId bookingId;
    private final LocalDateTime timestamp;

    public BookingDraftRejected(final OfferId offerId, final UserId userId, final BookingId bookingId) {
        this.status = BookingRequestStatus.REJECTED;
        this.message = EVENT_MESSAGE;
        this.offerId = offerId;
        this.userId = userId;
        this.bookingId = bookingId;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public BookingRequestStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public OfferId getOfferId() {
        return offerId;
    }

    public UserId getUserId() {
        return userId;
    }

    public BookingId getBookingId() {
        return bookingId;
    }
}
