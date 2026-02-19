package com.github.bovvver.offermanagment.resolvebooking;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class ResolveBookingREST {

    private static final String OFFER_OWNERSHIP_ENDPOINT = "/internal/check-ownership";

    private final OfferOwnershipService offerOwnershipService;

    @PostMapping(path = OFFER_OWNERSHIP_ENDPOINT)
    ResponseEntity<OfferOwnershipCheckResponse> checkOfferOwnership(
            @NotNull @RequestParam UUID userId,
            @NotNull @RequestParam UUID offerId
    ) {
        OfferOwnershipCheckResponse result = offerOwnershipService.checkOfferOwnership(userId, offerId);
        return ResponseEntity
                .status(result.httpStatusCode())
                .body(result);
    }
}
