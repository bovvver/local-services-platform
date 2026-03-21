package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.bovvver.bookingmanagement.outbox.OutboxEvent;
import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.OfferId;
import com.github.bovvver.bookingmanagement.vo.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookingDecisionMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final BookingDecisionMapper mapper = new BookingDecisionMapper(objectMapper);

    @Test
    void shouldMapBookingAcceptedEventToOutboxEvent() {
        UUID offerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        BookingAccepted event = new BookingAccepted(
                "Booking accepted.",
                new OfferId(offerId),
                new UserId(userId),
                new BookingId(bookingId),
                timestamp
        );

        OutboxEvent outboxEvent = mapper.toOutboxEvent(event);

        assertThat(outboxEvent).isNotNull();
        assertThat(outboxEvent.getAggregateId()).isEqualTo(offerId);
        assertThat(outboxEvent.getAggregateType()).isEqualTo("Booking");
        assertThat(outboxEvent.getEventType()).isEqualTo("BookingAccepted");

        JsonNode payload = outboxEvent.getPayload();
        assertThat(payload.get("offerId").asText()).isEqualTo(offerId.toString());
        assertThat(payload.get("userId").asText()).isEqualTo(userId.toString());
        assertThat(payload.get("bookingId").asText()).isEqualTo(bookingId.toString());
        assertThat(payload.get("message").asText()).isEqualTo("Booking accepted.");
    }

    @Test
    void shouldReturnNull_whenEventIsNotBookingAccepted() {
        DomainEventStub otherEvent = new DomainEventStub();

        OutboxEvent outboxEvent = mapper.toOutboxEvent(otherEvent);

        assertThat(outboxEvent).isNull();
    }

    private static class DomainEventStub implements com.github.bovvver.bookingmanagement.event.DomainEvent {
        @Override
        public LocalDateTime getTimestamp() {
            return LocalDateTime.now();
        }
    }
}
