package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class OfferOwnershipValidator {

    private final OfferOwnershipClient offerOwnershipClient;

    void validate(UUID userId, UUID offerId) {
        if (!offerOwnershipClient.isUserAnOwner(userId, offerId)) {
            throw new IllegalStateException("Current user is not the owner of the offer");
        }
    }
}
