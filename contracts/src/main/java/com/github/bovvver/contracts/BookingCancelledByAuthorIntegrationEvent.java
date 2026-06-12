package com.github.bovvver.contracts;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingCancelledByAuthorIntegrationEvent(
        String message,
        UUID offerId,
        LocalDateTime timestamp
) implements IntegrationEvent {
    private static final String EVENT_MESSAGE = "Offer cancelled by author. Related bookings should be cancelled.";

    public BookingCancelledByAuthorIntegrationEvent(final UUID offerId) {
        this(EVENT_MESSAGE, offerId, LocalDateTime.now());
    }
}

