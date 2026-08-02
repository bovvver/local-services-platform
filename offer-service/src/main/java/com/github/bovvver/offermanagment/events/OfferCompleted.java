package com.github.bovvver.offermanagment.events;

import java.util.UUID;

public record OfferCompleted(
        UUID offerId,
        UUID executorId
) implements DomainEvent {
}
