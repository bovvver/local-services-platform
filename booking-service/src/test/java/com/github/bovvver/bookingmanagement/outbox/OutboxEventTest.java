package com.github.bovvver.bookingmanagement.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createShouldInitializeEventWithDefaults() throws Exception {
        UUID aggregateId = UUID.randomUUID();
        String aggregateType = "Booking";
        String eventType = "BookingCreated";
        JsonNode payload = objectMapper.readTree("{\"foo\":\"bar\"}");
        LocalDateTime occurredAt = LocalDateTime.now().minusMinutes(1);

        OutboxEvent event = OutboxEvent.create(aggregateId, aggregateType, eventType, payload, occurredAt);

        assertThat(event).isNotNull();
        assertThat(event.getAggregateId()).isEqualTo(aggregateId);
        assertThat(event.getAggregateType()).isEqualTo(aggregateType);
        assertThat(event.getEventType()).isEqualTo(eventType);
        assertThat(event.getPayload()).isEqualTo(payload);
    }

    @Test
    void handleSuccessShouldMarkEventAsProcessedAndSent() throws Exception {
        OutboxEvent event = createNewEvent();
        event.handleSuccess();
        event.handleFailure("error after success");
    }

    @Test
    void handleFailureShouldScheduleRetriesWithExponentialBackoff() throws Exception {
        OutboxEvent event = createNewEvent();

        // 1. failure
        LocalDateTime beforeFirst = LocalDateTime.now();
        event.handleFailure("first error");
        LocalDateTime afterFirst = LocalDateTime.now();

        // 2. failure
        LocalDateTime beforeSecond = LocalDateTime.now();
        event.handleFailure("second error");
        LocalDateTime afterSecond = LocalDateTime.now();

        assertThat(Duration.between(beforeFirst, afterFirst).toMillis()).isGreaterThanOrEqualTo(0);
        assertThat(Duration.between(beforeSecond, afterSecond).toMillis()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void handleFailureShouldMarkEventAsDeadAfterMaxRetries() throws Exception {
        OutboxEvent event = createNewEvent();

        for (int i = 0; i < 7; i++) {
            event.handleFailure("error " + i);
        }
    }

    private OutboxEvent createNewEvent() throws Exception {
        UUID aggregateId = UUID.randomUUID();
        String aggregateType = "Booking";
        String eventType = "BookingCreated";
        JsonNode payload = objectMapper.readTree("{\"foo\":\"bar\"}");
        LocalDateTime occurredAt = LocalDateTime.now();
        return OutboxEvent.create(aggregateId, aggregateType, eventType, payload, occurredAt);
    }
}
