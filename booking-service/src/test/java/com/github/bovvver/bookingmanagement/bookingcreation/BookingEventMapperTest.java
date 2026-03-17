package com.github.bovvver.bookingmanagement.bookingcreation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.outbox.OutboxEvent;
import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.OfferId;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.bookingmanagement.vo.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookingEventMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final BookingEventMapper mapper = new BookingEventMapper(objectMapper);

    @Test
    void toOutboxEvent_shouldReturnNull_whenDomainEventIsNotBookingCreated() {
        DomainEvent otherEvent = new DummyEvent();

        OutboxEvent result = mapper.toOutboxEvent(otherEvent);

        assertThat(result).isNull();
    }

    @Test
    void toOutboxEvent_shouldMapBookingCreatedToOutboxEvent() {
        UUID bookingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();
        String message = "booking created";
        LocalDateTime timestamp = LocalDateTime.now();
        Salary salary = new Salary(BigDecimal.valueOf(123.45));

        BookingCreated domainEvent = new BookingCreated(
                message,
                new BookingId(bookingId),
                new UserId(userId),
                new OfferId(offerId),
                salary,
                timestamp
        );

        OutboxEvent outboxEvent = mapper.toOutboxEvent(domainEvent);

        assertThat(outboxEvent).isNotNull();
        assertThat(outboxEvent.getAggregateId()).isEqualTo(bookingId);
        assertThat(outboxEvent.getEventType()).isEqualTo("BookingCreated");

        JsonNode payload = outboxEvent.getPayload();
        assertThat(payload.get("message").asText()).isEqualTo(message);
        assertThat(UUID.fromString(payload.get("bookingId").asText())).isEqualTo(bookingId);
        assertThat(UUID.fromString(payload.get("userId").asText())).isEqualTo(userId);
        assertThat(UUID.fromString(payload.get("offerId").asText())).isEqualTo(offerId);
        assertThat(payload.get("salary").decimalValue()).isEqualTo(salary.value());
    }

    private static final class DummyEvent implements DomainEvent {
        private final LocalDateTime timestamp = LocalDateTime.now();

        @Override
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
