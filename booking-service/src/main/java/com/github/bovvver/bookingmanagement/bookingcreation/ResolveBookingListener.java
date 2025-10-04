package com.github.bovvver.bookingmanagement.bookingcreation;

import com.github.bovvver.contracts.BookingDraftAcceptedEvent;
import com.github.bovvver.contracts.BookingDraftRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ResolveBookingListener {

    private static final String CREATE_BOOKING_TOPIC = "booking.commands";

    private final BookingCreationService bookingCreationService;

    @KafkaListener(topics = CREATE_BOOKING_TOPIC, groupId = "booking-service")
    void onBookingAcceptedEventReceived(BookingDraftAcceptedEvent event) {
        bookingCreationService.createBooking(event);
    }

    @KafkaListener(topics = CREATE_BOOKING_TOPIC, groupId = "booking-service")
    void onBookingRejectedEventReceived(BookingDraftRejectedEvent event) {
        bookingCreationService.deleteDraftBooking(event.bookingId());
    }
}
