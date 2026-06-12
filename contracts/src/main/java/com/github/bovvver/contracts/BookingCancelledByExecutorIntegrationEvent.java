package com.github.bovvver.contracts;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingCancelledByExecutorIntegrationEvent(
        String message,
        UUID offerId,
        UUID executorId,
        LocalDateTime timestamp
) implements IntegrationEvent {
    private static final String EVENT_MESSAGE = "Offer cancelled by executor. Related bookings should be cancelled.";

    public BookingCancelledByExecutorIntegrationEvent(final UUID offerId, final UUID executorId) {
        this(EVENT_MESSAGE, offerId, executorId, LocalDateTime.now());
    }
}

