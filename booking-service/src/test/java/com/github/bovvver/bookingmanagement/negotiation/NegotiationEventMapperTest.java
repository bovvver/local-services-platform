package com.github.bovvver.bookingmanagement.negotiation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.outbox.OutboxEvent;
import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.OfferId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NegotiationEventMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final NegotiationEventMapper mapper = new NegotiationEventMapper(objectMapper);

    @Test
    void toOutboxEvent_shouldReturnNull_whenDomainEventIsNotNegotiationStarted() {
        DomainEvent otherEvent = new DummyEvent();

        OutboxEvent result = mapper.toOutboxEvent(otherEvent);

        assertThat(result).isNull();
    }

    @Test
    void toOutboxEvent_shouldMapNegotiationStartedToOutboxEvent() {
        UUID offerId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();
        String message = "negotiation started";
        LocalDateTime timestamp = LocalDateTime.now();

        NegotiationStarted domainEvent = new NegotiationStarted(
                message,
                new OfferId(offerId),
                new BookingId(bookingId),
                timestamp
        );

        OutboxEvent outboxEvent = mapper.toOutboxEvent(domainEvent);

        assertThat(outboxEvent).isNotNull();
        assertThat(outboxEvent.getAggregateId()).isEqualTo(offerId);
        assertThat(outboxEvent.getEventType()).isEqualTo("NegotiationStarted");

        JsonNode payload = outboxEvent.getPayload();
        assertThat(payload.get("message").asText()).isEqualTo(message);
        assertThat(UUID.fromString(payload.get("offerId").asText())).isEqualTo(offerId);
        assertThat(UUID.fromString(payload.get("bookingId").asText())).isEqualTo(bookingId);
    }

    private static final class DummyEvent implements DomainEvent {
        private final LocalDateTime timestamp = LocalDateTime.now();

        @Override
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
