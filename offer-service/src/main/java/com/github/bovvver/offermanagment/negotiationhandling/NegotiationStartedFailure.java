package com.github.bovvver.offermanagment.negotiationhandling;

import com.github.bovvver.offermanagment.events.IntegrationEvent;

import java.time.LocalDateTime;
import java.util.UUID;

record NegotiationStartedFailure(
        String message,
        UUID bookingId,
        LocalDateTime timestamp
) implements IntegrationEvent {

    private static final String EVENT_MESSAGE = "Failed to start negotiation due to invalid offer state.";

    public NegotiationStartedFailure(final UUID bookingId) {
        this(EVENT_MESSAGE, bookingId, LocalDateTime.now());
    }
}