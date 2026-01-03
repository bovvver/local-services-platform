package com.github.bovvver.offermanagment.resolvebookingdraft;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

record OfferAvailabilityCheckResponse(
        boolean isAvailable,
        String reason,
        LocalDateTime checkedAt,
        HttpStatus httpStatus
) {

    static OfferAvailabilityCheckResponse available() {
        return new OfferAvailabilityCheckResponse(true, "Offer is open for booking.", LocalDateTime.now(), HttpStatus.OK);
    }

    static OfferAvailabilityCheckResponse unavailable() {
        return new OfferAvailabilityCheckResponse(false, "Offer is already closed for booking.", LocalDateTime.now(), HttpStatus.CONFLICT);
    }

    static OfferAvailabilityCheckResponse notFound() {
        return new OfferAvailabilityCheckResponse(false, "Offer not found", LocalDateTime.now(), HttpStatus.NOT_FOUND);
    }
}
