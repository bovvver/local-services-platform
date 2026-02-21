package com.github.bovvver.bookingmanagement.bookingcreation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

record BookingCreatedIntegrationEvent(
        String message,
        UUID bookingId,
        UUID userId,
        UUID offerId,
        BigDecimal salary,
        LocalDateTime timestamp
) {
}
