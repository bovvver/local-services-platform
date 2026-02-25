package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.contracts.BookingAcceptedFailureIntegrationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BookingAcceptedFailureListener {

    private static final String BOOKING_ACCEPTED_FAILURE_TOPIC = "booking.accepted.failure";

    private final BookingFailureService bookingFailureService;

    @KafkaListener(topics = BOOKING_ACCEPTED_FAILURE_TOPIC, groupId = "booking-service")
    public void onBookingAcceptedFailure(BookingAcceptedFailureIntegrationEvent event) {
        bookingFailureService.handleBookingAcceptedFailure(event.bookingId());
    }
}
