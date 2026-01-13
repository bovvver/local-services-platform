package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.NegotiationId;
import com.github.bovvver.bookingmanagement.vo.NegotiationPositionId;

import java.time.LocalDateTime;

public record NegotiationStarted(
        String message,
        BookingId bookingId,
        NegotiationId negotiationId,
        NegotiationPositionId positionId,
        LocalDateTime timestamp
) implements DomainEvent {

    private static final String EVENT_MESSAGE = "Negotiation started.";

    public NegotiationStarted(BookingId bookingId,
                              NegotiationId negotiationId,
                              NegotiationPositionId positionId) {
        this(EVENT_MESSAGE, bookingId, negotiationId, positionId, LocalDateTime.now());
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
