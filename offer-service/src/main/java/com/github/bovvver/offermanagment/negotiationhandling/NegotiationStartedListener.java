package com.github.bovvver.offermanagment.negotiationhandling;

import com.github.bovvver.contracts.NegotiationStartedIntegrationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class NegotiationStartedListener {

    private static final String NEGOTIATION_STARTED_TOPIC = "booking.negotiation.started";

    private final NegotiationHandlingService negotiationHandlingService;

    @KafkaListener(topics = NEGOTIATION_STARTED_TOPIC, groupId = "offer-service")
    public void onNegotiationStarted(NegotiationStartedIntegrationEvent event) {
        negotiationHandlingService.handleNegotiationStarted(event.offerId());
    }
}
