package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import java.util.UUID;

interface OfferAvailabilityClient {

    boolean isOfferAvailable(
            UUID bookingId,
            UUID offerId,
            UUID userId
    );
}
