package com.github.bovvver.contracts;

import java.time.Instant;
import java.util.UUID;

public record BookingRejectedEvent(
        String error,
        String reason,
        UUID offerId,
        UUID userId,
        UUID bookingId,
        Instant timestamp
) {
}
