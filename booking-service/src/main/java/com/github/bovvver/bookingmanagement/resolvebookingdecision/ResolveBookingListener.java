package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.contracts.BookingDecisionMadeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ResolveBookingListener {

    private static final String OFFER_BOOKING_DECISION = "offer.booking.decision";

    private final ResolveBookingService resolveBookingService;

    @KafkaListener(topics = OFFER_BOOKING_DECISION, groupId = "booking-service")
    void onBookingDecisionMadeEventReceived(BookingDecisionMadeEvent cmd) {
        resolveBookingService.resolveBooking(cmd);
    }
}
