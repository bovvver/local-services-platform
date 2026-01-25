package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingAcceptedIntegrationEvent(
        String message,
        UUID offerId,
        UUID userId,
        LocalDateTime timestamp
) {
}
