package com.github.bovvver.offermanagment.events;

import java.util.UUID;

public record ExecutorAssigned(
        UUID offerId,
        UUID executorId
) implements DomainEvent {
}
