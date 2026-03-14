package com.github.bovvver.offermanagment.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.contracts.IntegrationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaEventBusTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private TopicResolver topicResolver;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaEventBus kafkaEventBus;

    @Test
    void shouldPublishEventToKafkaWhenSerializationSucceeds() throws Exception {
        IntegrationEvent event = new DummyEvent();
        given(topicResolver.resolve(event)).willReturn("test.topic");
        given(objectMapper.writeValueAsString(event)).willReturn("{ } ");

        kafkaEventBus.publish(event);

        verify(kafkaTemplate).send("test.topic", "{ } ");
    }

    @Test
    void shouldThrowRuntimeExceptionWhenSerializationFails() throws Exception {
        IntegrationEvent event = new DummyEvent();
        given(topicResolver.resolve(event)).willReturn("test.topic");
        given(objectMapper.writeValueAsString(event)).willThrow(new JsonProcessingException("boom") {
        });

        assertThatThrownBy(() -> kafkaEventBus.publish(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to serialize event: " + DummyEvent.class.getSimpleName());

        verify(kafkaTemplate, never()).send(org.mockito.Mockito.anyString(), org.mockito.Mockito.anyString());
    }

    private static class DummyEvent implements IntegrationEvent {
    }
}
