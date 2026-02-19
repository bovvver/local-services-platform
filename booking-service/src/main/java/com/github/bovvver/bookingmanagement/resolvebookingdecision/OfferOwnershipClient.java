package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import java.util.UUID;

interface  OfferOwnershipClient {

    boolean isUserAnOwner(
            UUID userId,
            UUID offerId
    );
}
