package com.github.bovvver.offermanagment.negotiationhandling;

import com.github.bovvver.contracts.NegotiationStartedIntegrationEvent;
import com.github.bovvver.offermanagment.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class NegotiationStartedListener {

    private static final String NEGOTIATION_STARTED_TOPIC = "booking.negotiation.started";

    private final NegotiationHandlingService negotiationHandlingService;
    private final OutboxService outboxService;

    @KafkaListener(topics = NEGOTIATION_STARTED_TOPIC, groupId = "offer-service", containerFactory = "negotiationStartedContainerFactory")
    public void onNegotiationStarted(NegotiationStartedIntegrationEvent event) {

        try {
            negotiationHandlingService.handleNegotiationStarted(event.offerId());
        } catch (IllegalStateException e) {
            outboxService.passToOutbox(
                    List.of(new NegotiationStartedFailure(event.bookingId())),
                    event.bookingId(),
                    "Booking"
            );
        }
    }
}
