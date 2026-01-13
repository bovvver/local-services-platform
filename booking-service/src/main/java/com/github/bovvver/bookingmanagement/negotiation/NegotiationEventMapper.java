package com.github.bovvver.bookingmanagement.negotiation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.outbox.OutboxEvent;

import java.time.LocalDateTime;
import java.util.stream.Stream;

class NegotiationEventMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static Stream<Object> mapToIntegrationEvents(DomainEvent domainEvent) {

        if (domainEvent instanceof NegotiationStarted e) {
            return Stream.of(
                    new NegotiationStartedIntegrationEvent(
                            e.message(),
                            e.bookingId().value(),
                            e.negotiationId().value(),
                            e.positionId().value(),
                            e.getTimestamp()
                    )
            );
        }
        return Stream.empty();
    }

    private static OutboxEvent toOutboxEvent(NegotiationStartedIntegrationEvent integrationEvent) {
        JsonNode payload = objectMapper.valueToTree(integrationEvent);
        return OutboxEvent.create(
                integrationEvent.bookingId(),
                "Negotiation",
                "NegotiationStarted",
                payload,
                LocalDateTime.now()
        );
    }

    static OutboxEvent toOutboxEvent(NegotiationStarted domainEvent) {
        Stream<Object> integrationEvent = mapToIntegrationEvents(domainEvent);
        return toOutboxEvent(integrationEvent);
    }
}
