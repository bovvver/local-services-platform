package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.offermanagment.events.DomainEvent;

import java.util.UUID;

public record BookingAcceptedFailure(
        UUID bookingId
) implements DomainEvent {
}