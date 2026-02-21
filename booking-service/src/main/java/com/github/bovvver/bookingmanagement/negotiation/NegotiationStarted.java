package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.OfferId;

import java.time.LocalDateTime;

public record NegotiationStarted(
        String message,
        OfferId offerId,
        BookingId bookingId,
        LocalDateTime timestamp
) implements DomainEvent {

    private static final String EVENT_MESSAGE = "Negotiation started.";

    public NegotiationStarted(OfferId offerId, BookingId bookingId) {
        this(EVENT_MESSAGE, offerId, bookingId, LocalDateTime.now());
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
