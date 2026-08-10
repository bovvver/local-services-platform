package com.github.bovvver.contracts;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewAddedIntegrationEvent(
        String message,
        UUID offerId,
        UUID executorId,
        double rating,
        LocalDateTime timestamp
) implements IntegrationEvent {
    private static final String EVENT_MESSAGE = "Review added.";

    public ReviewAddedIntegrationEvent(final UUID offerId, final UUID executorId, final double rating) {
        this(EVENT_MESSAGE, offerId, executorId, rating, LocalDateTime.now());
    }
}
