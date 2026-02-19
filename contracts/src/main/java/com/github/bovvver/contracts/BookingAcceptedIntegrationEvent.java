package com.github.bovvver.contracts;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingAcceptedIntegrationEvent(
        String message,
        UUID offerId,
        UUID userId,
        LocalDateTime timestamp
) {
}