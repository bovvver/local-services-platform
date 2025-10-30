package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.contracts.BookingDecisionCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ResolveNegotiationDecisionListener {

    private static final String OFFER_BOOKING_NEGOTIATE = "offer.booking.negotiate";

    private final ResolveNegotiationDecisionService resolveNegotiationDecisionService;

    @KafkaListener(topics = OFFER_BOOKING_NEGOTIATE, groupId = "booking-service")
    void onBookingDecisionCommandReceived(BookingDecisionCommand cmd) {
        resolveNegotiationDecisionService.beginNegotiation(cmd);
    }
}
