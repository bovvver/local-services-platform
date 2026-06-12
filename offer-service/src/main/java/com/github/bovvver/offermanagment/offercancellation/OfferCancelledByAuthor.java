package com.github.bovvver.offermanagment.offercancellation;

import com.github.bovvver.offermanagment.events.DomainEvent;

import java.util.UUID;

public record OfferCancelledByAuthor(
        UUID offerId
) implements DomainEvent {
}
