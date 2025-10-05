package com.github.bovvver.offermanagment.resolvebooking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class ResolveBookingREST {

    private static final String RESOLVE_BOOKING_ENDPOINT = "/{offerId}/bookings/{bookingId}/decision";

    private final ResolveBookingService resolveBookingService;

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
