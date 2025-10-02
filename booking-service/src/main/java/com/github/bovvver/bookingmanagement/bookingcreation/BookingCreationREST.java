package com.github.bovvver.bookingmanagement.bookingcreation;

import com.github.bovvver.contracts.BookOfferCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class BookingCreationREST {

    private static final String CREATE_BOOKING_TOPIC = "booking.commands";
    private static final String CREATE_BOOKING_ENDPOINT = "/bookings/create";

    private final KafkaTemplate<String, BookOfferCommand> kafka;
    private final BookingCreationService bookingCreationService;

    @PostMapping(path = CREATE_BOOKING_ENDPOINT)
    void createBooking(@Valid @RequestBody BookOfferRequest request) {

        BookOfferCommand cmd = bookingCreationService.createBookingCommand(request);
        bookingCreationService.createDraftBooking(cmd, request.salary());
        kafka.send(CREATE_BOOKING_TOPIC, cmd.offerId().toString(), cmd);
    }
}
