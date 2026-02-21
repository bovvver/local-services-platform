package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.contracts.BookingAcceptedIntegrationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BookingAcceptedListener {

    private static final String OFFER_BOOKING_DECISION_TOPIC = "booking.accepted";

    private final ResolveBookingService resolveBookingService;

    @KafkaListener(topics = OFFER_BOOKING_DECISION_TOPIC, groupId = "offer-service")
    public void onBookingAccepted(BookingAcceptedIntegrationEvent event) {
        resolveBookingService.completeBookingAssignment(event);
    }
}
