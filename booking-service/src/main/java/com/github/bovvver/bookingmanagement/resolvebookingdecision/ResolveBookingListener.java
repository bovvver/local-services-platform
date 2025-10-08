package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.contracts.BookingDecisionCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ResolveBookingListener {

    private static final String OFFER_COMMANDS_TOPIC = "offer.commands";

    private final ResolveBookingService resolveBookingService;

    @KafkaListener(topics = OFFER_COMMANDS_TOPIC, groupId = "booking-service")
    void onBookingDecisionCommandReceived(BookingDecisionCommand cmd) {
        resolveBookingService.resolveBooking(cmd);
    }
}
