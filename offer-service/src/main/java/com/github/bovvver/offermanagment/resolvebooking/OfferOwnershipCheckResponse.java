package com.github.bovvver.offermanagment.resolvebooking;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

record OfferOwnershipCheckResponse(
        boolean isOwner,
        String message,
        LocalDateTime checkedAt,
        int httpStatusCode
) {
    static final String OFFER_NOT_FOUND_MESSAGE = "Offer not found.";
    static final String OWNERSHIP_CONFIRMED_MESSAGE = "User is the owner of this offer.";
    static final String OWNERSHIP_DENIED_MESSAGE = "User isn't the owner of this offer.";

    static OfferOwnershipCheckResponse ownershipConfirmed() {
        return new OfferOwnershipCheckResponse(true, OWNERSHIP_CONFIRMED_MESSAGE, LocalDateTime.now(), HttpStatus.OK.value());
    }

    static OfferOwnershipCheckResponse ownershipDenied() {
        return new OfferOwnershipCheckResponse(false, OWNERSHIP_DENIED_MESSAGE, LocalDateTime.now(), HttpStatus.FORBIDDEN.value());
    }

    static OfferOwnershipCheckResponse notFound() {
        return new OfferOwnershipCheckResponse(false, OFFER_NOT_FOUND_MESSAGE, LocalDateTime.now(), HttpStatus.NOT_FOUND.value());
    }
}
