package com.github.bovvver.bookingmanagement.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaEventBusTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private TopicResolver topicResolver;

    @InjectMocks
    private KafkaEventBus kafkaEventBus;

    @Test
    void shouldPublishEventToKafkaWhenPublishSucceeds() throws Exception {
        OutboxEvent event = createDummyOutboxEvent();
        String topic = "test.topic";
        given(topicResolver.resolve(event)).willReturn(topic);

        kafkaEventBus.publish(event);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo(topic);
        assertThat(keyCaptor.getValue()).isEqualTo(event.getAggregateId().toString());
        assertThat(valueCaptor.getValue()).isEqualTo(event.getPayload().toString());
    }

    @Test
    void shouldWrapExceptionWhenPublishFails() throws Exception {
        OutboxEvent event = createDummyOutboxEvent();
        String topic = "test.topic";
        given(topicResolver.resolve(event)).willReturn(topic);
        RuntimeException rootCause = new RuntimeException("boom");
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenThrow(rootCause);

        assertThatThrownBy(() -> kafkaEventBus.publish(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to publish event: " + event.getEventType())
                .hasCause(rootCause);

        verify(topicResolver).resolve(event);
        verify(kafkaTemplate).send(topic, event.getAggregateId().toString(), event.getPayload().toString());
    }

    @Test
    void shouldNotSendMessageWhenTopicResolutionFails() throws Exception {
        OutboxEvent event = createDummyOutboxEvent();
        IllegalStateException rootCause = new IllegalStateException("No topic configured");
        given(topicResolver.resolve(event)).willThrow(rootCause);

        assertThatThrownBy(() -> kafkaEventBus.publish(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to publish event: " + event.getEventType())
                .hasCause(rootCause);

        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }

    private OutboxEvent createDummyOutboxEvent() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "{ \"bookingId\": \"" + UUID.randomUUID() + "\" }";
        JsonNode payload = mapper.readTree(jsonString);

        return new OutboxEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Booking",
                "BookingCreated",
                payload,
                LocalDateTime.now(),
                false,
                OutboxStatus.NEW,
                null,
                0,
                null
        );
    }
}
