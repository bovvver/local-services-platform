package com.github.bovvver.bookingmanagement.bookingcreation;

import com.github.bovvver.contracts.BookingAcceptedEvent;
import com.github.bovvver.contracts.BookingRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ResolveBookingListener {

    private static final String CREATE_BOOKING_TOPIC = "booking.commands";

    private final KafkaTemplate<String, BookingRejectedEvent> kafka;
    private final BookingCreationService bookingCreationService;

    @KafkaListener(topics = CREATE_BOOKING_TOPIC, groupId = "booking-service")
    void onBookingAcceptedEventReceived(BookingAcceptedEvent event) {
        bookingCreationService.createBooking(event);
    }

    @KafkaListener(topics = CREATE_BOOKING_TOPIC, groupId = "booking-service")
    void onBookingRejectedEventReceived(BookingRejectedEvent event) {
        bookingCreationService.deleteDraftBooking(event.bookingId());
    }
}
