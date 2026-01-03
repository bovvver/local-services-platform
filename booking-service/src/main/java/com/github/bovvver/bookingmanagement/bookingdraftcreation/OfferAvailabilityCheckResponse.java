package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

record OfferAvailabilityCheckResponse(
        boolean isAvailable,
        String reason,
        LocalDateTime checkedAt,
        HttpStatus httpStatus
) {
}

