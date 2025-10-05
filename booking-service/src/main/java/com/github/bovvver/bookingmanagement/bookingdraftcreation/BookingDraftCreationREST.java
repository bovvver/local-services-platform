package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class BookingDraftCreationREST {

    private static final String CREATE_BOOKING_ENDPOINT = "/bookings/create";

    private final BookingDraftCreationService bookingDraftCreationService;

    @PostMapping(path = CREATE_BOOKING_ENDPOINT)
    void createBooking(@Valid @RequestBody BookOfferRequest request) {
        bookingDraftCreationService.processBookingCreation(request);
    }
}
