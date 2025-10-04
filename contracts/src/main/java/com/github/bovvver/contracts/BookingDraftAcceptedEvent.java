package com.github.bovvver.contracts;

import java.time.Instant;
import java.util.UUID;

public record BookingDraftAcceptedEvent(
        String status,
        String message,
        UUID offerId,
        UUID userId,
        UUID bookingId,
        Instant timestamp
) {
}
