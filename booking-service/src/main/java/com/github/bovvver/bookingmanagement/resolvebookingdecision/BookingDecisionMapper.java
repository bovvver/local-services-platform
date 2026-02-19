package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.outbox.OutboxEvent;
import com.github.bovvver.contracts.BookingAcceptedIntegrationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BookingDecisionMapper {

    private final ObjectMapper objectMapper;

    public OutboxEvent toOutboxEvent(DomainEvent domainEvent) {

        if (!(domainEvent instanceof BookingAccepted e)) {
            return null;
        }

        BookingAcceptedIntegrationEvent integrationEvent = new BookingAcceptedIntegrationEvent(
                e.message(),
                e.offerId().value(),
                e.userId().value(),
                e.getTimestamp()
        );

        JsonNode payload = objectMapper.valueToTree(integrationEvent);
        return OutboxEvent.create(
                integrationEvent.offerId(),
                "Booking",
                "BookingAccepted",
                payload,
                domainEvent.getTimestamp()
        );
    }
}