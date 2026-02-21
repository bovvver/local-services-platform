package com.github.bovvver.contracts;

import java.time.LocalDateTime;
import java.util.UUID;

public record NegotiationStartedIntegrationEvent(
        String message,
        UUID offerId,
        LocalDateTime timestamp
) {
}
