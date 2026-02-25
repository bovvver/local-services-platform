package com.github.bovvver.contracts;

import java.time.LocalDateTime;
import java.util.UUID;

public record NegotiationStartedFailureIntegrationEvent(
        String message,
        UUID bookingId,
        LocalDateTime timestamp
) {
    private static final String EVENT_MESSAGE = "Failed to start negotiation due to invalid offer state.";

    public NegotiationStartedFailureIntegrationEvent(final UUID bookingId) {
        this(EVENT_MESSAGE, bookingId, LocalDateTime.now());
    }
}
