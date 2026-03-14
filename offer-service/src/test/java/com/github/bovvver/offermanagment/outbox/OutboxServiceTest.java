package com.github.bovvver.offermanagment.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.offermanagment.events.DomainEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxService outboxService;

    @Test
    void shouldSaveEventsToOutbox() throws Exception {
        UUID aggregateId = UUID.randomUUID();
        String aggregateType = "Booking";
        DomainEvent event = new DummyDomainEvent();
        given(objectMapper.writeValueAsString(event)).willReturn("{ } ");

        outboxService.passToOutbox(List.of(event), aggregateId, aggregateType);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxRepository).save(captor.capture());
        OutboxEvent saved = captor.getValue();

        assertThat(saved.getAggregateId()).isEqualTo(aggregateId);
        assertThat(saved.getAggregateType()).isEqualTo(aggregateType);
        assertThat(saved.getType()).isEqualTo(DummyDomainEvent.class.getSimpleName());
        assertThat(saved.getPayload()).isEqualTo("{ } ");
        assertThat(saved.getOccurredAt()).isNotNull();
    }

    @Test
    void shouldThrowRuntimeExceptionWhenSerializationFails() throws Exception {
        UUID aggregateId = UUID.randomUUID();
        String aggregateType = "Booking";
        DomainEvent event = new DummyDomainEvent();
        given(objectMapper.writeValueAsString(event)).willThrow(new JsonProcessingException("boom") {
        });

        assertThatThrownBy(() -> outboxService.passToOutbox(List.of(event), aggregateId, aggregateType))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to serialize event: " + DummyDomainEvent.class.getSimpleName());
    }

    private static class DummyDomainEvent implements DomainEvent {
    }
}
