package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.OfferId;
import com.github.bovvver.bookingmanagement.vo.UserId;

import java.time.LocalDateTime;

public record BookingAccepted(
        String message,
        OfferId offerId,
        UserId userId,
        BookingId bookingId,
        LocalDateTime timestamp
) implements DomainEvent {

    private static final String EVENT_MESSAGE = "Booking accepted.";

    public BookingAccepted(OfferId offerId, UserId userId, BookingId bookingId) {
        this(EVENT_MESSAGE, offerId, userId, bookingId, LocalDateTime.now());
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
