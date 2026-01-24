package com.github.bovvver.bookingmanagement.negotiation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.outbox.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class NegotiationEventMapper {

    private final ObjectMapper objectMapper;

    public OutboxEvent toOutboxEvent(DomainEvent domainEvent) {

        if (!(domainEvent instanceof NegotiationStarted e)) {
            return null;
        }

        NegotiationStartedIntegrationEvent integrationEvent =
                new NegotiationStartedIntegrationEvent(
                        e.message(),
                        e.bookingId().value(),
                        e.negotiationId().value(),
                        e.getTimestamp()
                );

        JsonNode payload = objectMapper.valueToTree(integrationEvent);
        return OutboxEvent.create(
                integrationEvent.bookingId(),
                "Negotiation",
                "NegotiationStarted",
                payload,
                integrationEvent.timestamp()
        );
    }
}