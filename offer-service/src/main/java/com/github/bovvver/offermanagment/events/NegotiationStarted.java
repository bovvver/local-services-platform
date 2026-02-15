package com.github.bovvver.offermanagment.events;

import java.math.BigDecimal;
import java.util.UUID;

public record NegotiationStarted(
    UUID bookingId,
    UUID offerId,
    BigDecimal salary
) implements IntegrationEvent {
}
