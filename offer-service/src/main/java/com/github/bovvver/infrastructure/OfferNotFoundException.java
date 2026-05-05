package com.github.bovvver.infrastructure;

import java.util.UUID;

public class OfferNotFoundException extends RuntimeException {

    public OfferNotFoundException(UUID offerId) {
        super("Offer not found: %s".formatted(offerId));
    }
}
