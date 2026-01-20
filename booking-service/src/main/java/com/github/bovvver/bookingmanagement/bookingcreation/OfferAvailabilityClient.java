package com.github.bovvver.bookingmanagement.bookingcreation;

import java.util.UUID;

interface OfferAvailabilityClient {

    boolean isOfferAvailable(
            UUID bookingId,
            UUID offerId,
            UUID userId
    );
}
