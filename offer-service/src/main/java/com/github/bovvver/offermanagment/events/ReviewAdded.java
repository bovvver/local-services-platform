package com.github.bovvver.offermanagment.events;

import java.util.UUID;

public record ReviewAdded(
        UUID offerId,
        UUID executorId,
        double rating
) implements DomainEvent {
}
