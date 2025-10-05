package com.github.bovvver.bookingmanagement.bookingcreation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class BookingCreationREST {

    private static final String CREATE_BOOKING_ENDPOINT = "/bookings/create";

    private final BookingCreationService bookingCreationService;

    @PostMapping(path = CREATE_BOOKING_ENDPOINT)
    void createBooking(@Valid @RequestBody BookOfferRequest request) {
        bookingCreationService.processBookingCreation(request);
    }
}
