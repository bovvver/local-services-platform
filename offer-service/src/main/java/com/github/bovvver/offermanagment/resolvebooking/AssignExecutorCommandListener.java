package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.contracts.AssignExecutorCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class AssignExecutorCommandListener {

    private static final String OFFER_BOOKING_DECISION_RESPONSE = "offer.booking.decision.response";

    private final ResolveBookingService resolveBookingService;

    @KafkaListener(topics = OFFER_BOOKING_DECISION_RESPONSE, groupId = "booking-service")
    public void on(AssignExecutorCommand cmd) {
        resolveBookingService.completeBookingAssignment(cmd);
    }
}
