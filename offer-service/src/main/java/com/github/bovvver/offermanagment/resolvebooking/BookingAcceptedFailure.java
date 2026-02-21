package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.offermanagment.events.IntegrationEvent;

import java.time.LocalDateTime;
import java.util.UUID;

record BookingAcceptedFailure(
        String message,
        UUID bookingId,
        LocalDateTime timestamp
) implements IntegrationEvent {

    private static final String EVENT_MESSAGE = "Failed to accept booking assignment due to invalid offer state.";

    public BookingAcceptedFailure(final UUID bookingId) {
        this(EVENT_MESSAGE, bookingId, LocalDateTime.now());
    }
}