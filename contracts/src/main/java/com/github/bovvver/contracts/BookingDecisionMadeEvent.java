package com.github.bovvver.contracts;

import java.math.BigDecimal;
import java.util.UUID;

public record BookingDecisionMadeEvent(
        UUID bookingId,
        UUID offerId,
        BookingDecisionStatus status,
        BigDecimal salary
) {
}
