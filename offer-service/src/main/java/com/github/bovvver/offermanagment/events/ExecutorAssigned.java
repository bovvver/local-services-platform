package com.github.bovvver.offermanagment.events;

import java.util.UUID;

public record ExecutorAssigned(
        String message,
        UUID offerId,
        UUID executorId
) implements IntegrationEvent {

    private static final String EVENT_MESSAGE = "Executor assigned to offer. Offer cannot accept more bookings.";

    public ExecutorAssigned(final UUID offerId, final UUID executorId) {
        this(EVENT_MESSAGE, offerId, executorId);
    }
}
