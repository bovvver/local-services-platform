package com.github.bovvver.offermanagment.vo;

import com.github.bovvver.event.DomainEvent;

import java.time.LocalDateTime;

public record BookingDraftAccepted(
        BookingRequestStatus status,
        String message,
        OfferId offerId,
        UserId userId,
        BookingId bookingId,
        LocalDateTime timestamp
) implements DomainEvent {

    private static final String EVENT_MESSAGE = "Booking request accepted.";

    public BookingDraftAccepted(OfferId offerId, UserId userId, BookingId bookingId) {
        this(BookingRequestStatus.ACCEPTED, EVENT_MESSAGE, offerId, userId, bookingId, LocalDateTime.now());
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
