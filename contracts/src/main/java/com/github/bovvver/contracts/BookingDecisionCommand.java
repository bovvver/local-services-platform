package com.github.bovvver.contracts;

import java.util.UUID;

public record BookingDecisionCommand (
        UUID bookingId,
        UUID offerId,
        BookingDecisionStatus status,
        Double salary
) {
}
