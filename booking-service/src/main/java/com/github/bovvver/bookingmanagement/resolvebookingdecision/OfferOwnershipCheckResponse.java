package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import java.time.LocalDateTime;

record OfferOwnershipCheckResponse(
        boolean isOwner,
        String message,
        LocalDateTime checkedAt,
        int httpStatusCode
) {
}
