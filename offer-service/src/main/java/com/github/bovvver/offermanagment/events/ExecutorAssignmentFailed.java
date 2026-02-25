package com.github.bovvver.offermanagment.events;

import java.util.UUID;

public record ExecutorAssignmentFailed (
        UUID offerId,
        UUID executorId
) implements DomainEvent {
}
