package com.github.bovvver.contracts;

import java.util.UUID;

public record ExecutorAssignedIntegrationEvent(
        String message,
        UUID offerId,
        UUID executorId
) {
    private static final String EVENT_MESSAGE = "Executor assigned to offer. Offer cannot accept more bookings.";

    public ExecutorAssignedIntegrationEvent(final UUID offerId, final UUID executorId) {
        this(EVENT_MESSAGE, offerId, executorId);
    }
}
