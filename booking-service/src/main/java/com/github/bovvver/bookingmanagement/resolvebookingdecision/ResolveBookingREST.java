package com.github.bovvver.bookingmanagement.resolvebookingdecision;

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

    private static final String RESOLVE_BOOKING_ENDPOINT = "/bookings/{bookingId}/decision";

    private final ResolveBookingService resolveBookingService;

    @PostMapping(path = RESOLVE_BOOKING_ENDPOINT)
    ResponseEntity<BookingDecisionResponse> resolveBookingStatus(
            @PathVariable UUID bookingId,
            @Valid @RequestBody BookingDecisionRequest request
    ) {
        resolveBookingService.processBookingDecision(bookingId, request);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                new BookingDecisionResponse(
                        HttpStatus.ACCEPTED.value(),
                        "Booking decision for bookingId %s is being processed".formatted(bookingId)
                )
        );
    }
}
