package com.github.bovvver.offermanagment.offercancellation;

import com.github.bovvver.offermanagment.events.DomainEvent;

import java.util.UUID;

public record OfferCancelledByExecutor(
        UUID offerId,
        UUID executorId
) implements DomainEvent {
}
