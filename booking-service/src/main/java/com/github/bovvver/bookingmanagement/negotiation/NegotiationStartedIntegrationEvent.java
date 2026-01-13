package com.github.bovvver.bookingmanagement.negotiation;

import java.time.LocalDateTime;
import java.util.UUID;

public record NegotiationStartedIntegrationEvent(
        String message,
        UUID bookingId,
        UUID negotiationId,
        UUID positionId,
        LocalDateTime timestamp
) {
}
