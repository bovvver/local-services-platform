package com.github.bovvver.offermanagment.resolvebookingdraft;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class OfferAvailabilityREST {

    private static final String RESOLVE_BOOKING_DRAFT_ENDPOINT = "/internal/offer/availability";

    private final OfferAvailabilityService offerAvailabilityService;

    @GetMapping(path = RESOLVE_BOOKING_DRAFT_ENDPOINT)
    ResponseEntity<OfferAvailabilityCheckResponse> resolveBookingDraft(
            @NotNull @RequestParam UUID offerId,
            @NotNull @RequestParam UUID userId,
            @NotNull @RequestParam UUID bookingId
    ) {
        OfferAvailabilityCheckResponse result = offerAvailabilityService.checkOfferAvailability(offerId, userId, bookingId);
        return ResponseEntity
                .status(result.httpStatus())
                .body(result);
    }
}
