package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.contracts.NegotiationStartedFailureIntegrationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class NegotiationStartedFailureListener {

    private static final String NEGOTIATION_STARTED_FAILURE_TOPIC = "negotiation.started.failure";

    private final NegotiationCancellationService negotiationCancellationService;

    @KafkaListener(topics = NEGOTIATION_STARTED_FAILURE_TOPIC, groupId = "booking-service")
    public void onNegotiationStartedFailure(NegotiationStartedFailureIntegrationEvent event) {
        negotiationCancellationService.cancelNegotiation(event.bookingId());
    }
}
