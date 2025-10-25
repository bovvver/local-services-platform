package com.github.bovvver.contracts;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingDraftAcceptedEvent(
        String status,
        String message,
        UUID offerId,
        UUID userId,
        UUID bookingId,
        LocalDateTime timestamp
) {
}
