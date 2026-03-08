package com.github.bovvver.contracts;

import java.util.UUID;

public record ExecutorAssignmentFailedIntegrationEvent(
        String message,
        UUID offerId,
        UUID executorId
) implements IntegrationEvent {
    private static final String EVENT_MESSAGE = "Executor assignment failed.";

    public ExecutorAssignmentFailedIntegrationEvent(final UUID offerId, final UUID executorId) {
        this(EVENT_MESSAGE, offerId, executorId);
    }
}
