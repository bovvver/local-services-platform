package com.github.bovvver.offermanagment.negotiationhandling;

import com.github.bovvver.offermanagment.events.DomainEvent;

import java.util.UUID;

public record NegotiationStartedFailure(
        UUID bookingId
) implements DomainEvent {
}