package com.github.bovvver.bookingmanagement.bookingcreation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.outbox.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BookingEventMapper {

    private final ObjectMapper objectMapper;

    public OutboxEvent toOutboxEvent(DomainEvent domainEvent) {

        if (!(domainEvent instanceof BookingCreated e)) {
            return null;
        }

        BookingCreatedIntegrationEvent integrationEvent =
                new BookingCreatedIntegrationEvent(
                        e.message(),
                        e.bookingId().value(),
                        e.userId().value(),
                        e.offerId().value(),
                        e.salary().value(),
                        e.getTimestamp()
                );

        JsonNode payload = objectMapper.valueToTree(integrationEvent);
        return OutboxEvent.create(
                integrationEvent.bookingId(),
                "Booking",
                "BookingCreated",
                payload,
                integrationEvent.timestamp()
        );
    }
}