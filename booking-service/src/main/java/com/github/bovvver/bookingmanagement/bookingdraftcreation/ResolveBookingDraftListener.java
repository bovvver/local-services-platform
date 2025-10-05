package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import com.github.bovvver.contracts.BookingDraftAcceptedEvent;
import com.github.bovvver.contracts.BookingDraftRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ResolveBookingDraftListener {

    private static final String BOOKING_COMMANDS_TOPIC = "booking.commands";

    private final BookingDraftCreationService bookingDraftCreationService;

    @KafkaListener(topics = BOOKING_COMMANDS_TOPIC, groupId = "booking-service")
    void onBookingAcceptedEventReceived(BookingDraftAcceptedEvent event) {
        bookingDraftCreationService.createBooking(event);
    }

    @KafkaListener(topics = BOOKING_COMMANDS_TOPIC, groupId = "booking-service")
    void onBookingRejectedEventReceived(BookingDraftRejectedEvent event) {
        bookingDraftCreationService.deleteDraftBooking(event.bookingId());
    }
}
