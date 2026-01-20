package com.github.bovvver.bookingmanagement.bookingcreation;

import java.time.LocalDateTime;

record OfferAvailabilityCheckResponse(
        boolean isAvailable,
        String reason,
        LocalDateTime checkedAt,
        int httpStatusCode
) {
}

