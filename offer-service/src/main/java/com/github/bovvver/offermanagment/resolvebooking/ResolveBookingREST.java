package com.github.bovvver.offermanagment.resolvebooking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class ResolveBookingREST {

    private static final String RESOLVE_BOOKING_ENDPOINT = "/{offerId}/bookings/{bookingId}/decision";
    private static final String OFFER_AVAILABILITY_ENDPOINT = "/internal/offer/availability";

    private final ResolveBookingService resolveBookingService;
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

    @PostMapping(path = RESOLVE_BOOKING_ENDPOINT)
    ResponseEntity<BookingDecisionResponse> resolveBookingStatus(
            @PathVariable UUID bookingId,
            @PathVariable UUID offerId,
            @Valid @RequestBody BookingDecisionRequest request
    ) {
        resolveBookingService.processBookingDecision(bookingId, offerId, request);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                new BookingDecisionResponse(
                        HttpStatus.ACCEPTED.value(),
                        "Booking decision for bookingId %s on offerId %s is being processed".formatted(bookingId, offerId)
                )
        );
    }
}
