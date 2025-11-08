package com.github.bovvver.contracts;

import java.util.UUID;

public record BookingDecisionMadeEvent(
        UUID bookingId,
        UUID offerId,
        BookingDecisionStatus status,
        Double salary
) {
}
