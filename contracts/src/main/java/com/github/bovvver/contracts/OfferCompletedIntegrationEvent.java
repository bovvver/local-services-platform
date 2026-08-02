package com.github.bovvver.contracts;

import java.time.LocalDateTime;
import java.util.UUID;

public record OfferCompletedIntegrationEvent(
        String message,
        UUID offerId,
        UUID executorId,
        LocalDateTime timestamp
) implements IntegrationEvent {
    private static final String EVENT_MESSAGE = "Offer completed.";

    public OfferCompletedIntegrationEvent(final UUID offerId, final UUID executorId) {
        this(EVENT_MESSAGE, offerId, executorId, LocalDateTime.now());
    }
}
