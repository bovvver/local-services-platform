package com.github.bovvver.contracts;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingAcceptedFailureIntegrationEvent (
        String message,
        UUID bookingId,
        LocalDateTime timestamp
) implements IntegrationEvent {
    private static final String EVENT_MESSAGE = "Failed to accept booking assignment due to invalid offer state.";

    public BookingAcceptedFailureIntegrationEvent(final UUID bookingId) {
        this(EVENT_MESSAGE, bookingId, LocalDateTime.now());
    }
}
