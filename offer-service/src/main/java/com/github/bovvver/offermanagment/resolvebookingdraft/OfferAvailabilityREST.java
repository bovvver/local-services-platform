package com.github.bovvver.offermanagment.resolvebookingdraft;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class OfferAvailabilityREST {

    private static final String OFFER_AVAILABILITY_ENDPOINT = "/internal/offer/availability";

    private final OfferAvailabilityService offerAvailabilityService;

    @PostMapping(path = OFFER_AVAILABILITY_ENDPOINT)
    ResponseEntity<OfferAvailabilityCheckResponse> attemptOfferBooking(
            @NotNull @RequestParam UUID offerId,
            @NotNull @RequestParam UUID userId,
            @NotNull @RequestParam UUID bookingId
    ) {
        OfferAvailabilityCheckResponse result = offerAvailabilityService.attemptOfferBooking(offerId, userId, bookingId);
        return ResponseEntity
                .status(result.httpStatusCode())
                .body(result);
    }
}
