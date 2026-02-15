package com.github.bovvver.offermanagment.events;

import java.util.UUID;

public record ExecutorAssignmentFailed (
        String message,
        UUID offerId,
        UUID executorId
) implements IntegrationEvent {

    private static final String EVENT_MESSAGE = "Executor assignment failed.";

    public ExecutorAssignmentFailed(final UUID offerId, final UUID executorId) {
        this(EVENT_MESSAGE, offerId, executorId);
    }
}
