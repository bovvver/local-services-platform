package com.github.bovvver.offermanagment.resolvebooking;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

record OfferOwnershipCheckResponse(
        boolean isOwner,
        String message,
        LocalDateTime checkedAt,
        int httpStatusCode
) {

    static OfferOwnershipCheckResponse ownershipConfirmed() {
        return new OfferOwnershipCheckResponse(true, "User is the owner of this offer.", LocalDateTime.now(), HttpStatus.OK.value());
    }

    static OfferOwnershipCheckResponse ownershipDenied() {
        return new OfferOwnershipCheckResponse(false, "User isn't the owner of this offer.", LocalDateTime.now(), HttpStatus.FORBIDDEN.value());
    }

    static OfferOwnershipCheckResponse notFound() {
        return new OfferOwnershipCheckResponse(false, "Offer not found.", LocalDateTime.now(), HttpStatus.NOT_FOUND.value());
    }
}
