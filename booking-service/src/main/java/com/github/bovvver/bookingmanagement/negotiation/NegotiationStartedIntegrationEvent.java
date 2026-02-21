package com.github.bovvver.bookingmanagement.negotiation;

import java.time.LocalDateTime;
import java.util.UUID;

record NegotiationStartedIntegrationEvent(
        String message,
        UUID bookingId,
        UUID negotiationId,
        LocalDateTime timestamp
) {
}
