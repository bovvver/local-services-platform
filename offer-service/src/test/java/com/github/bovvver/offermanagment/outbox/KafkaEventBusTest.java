package com.github.bovvver.offermanagment.outbox;

import com.github.bovvver.contracts.IntegrationEvent;
import com.github.bovvver.infrastructure.EventPublicationFailedException;
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
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private TopicResolver topicResolver;

    @InjectMocks
    private KafkaEventBus kafkaEventBus;

    @Test
    void shouldPublishEventToKafkaWhenPublishSucceeds() throws Exception {
        IntegrationEvent event = new DummyEvent();
        given(topicResolver.resolve(event)).willReturn("test.topic");

        kafkaEventBus.publish(event);

        verify(kafkaTemplate).send("test.topic", event);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenSerializationFails() throws Exception {
        IntegrationEvent event = new DummyEvent();
        given(topicResolver.resolve(event)).willReturn("test.topic");
        RuntimeException rootCause = new RuntimeException("boom");
        org.mockito.Mockito.when(kafkaTemplate.send("test.topic", event)).thenThrow(rootCause);

        assertThatThrownBy(() -> kafkaEventBus.publish(event))
                .isInstanceOf(EventPublicationFailedException.class)
                .hasMessageContaining("Failed to publish event: " + DummyEvent.class.getSimpleName())
                .hasCause(rootCause);
    }

    @Test
    void shouldNotSendMessageWhenTopicResolutionFails() throws Exception {
        IntegrationEvent event = new DummyEvent();
        IllegalStateException rootCause = new IllegalStateException("No topic configured");
        given(topicResolver.resolve(event)).willThrow(rootCause);

        assertThatThrownBy(() -> kafkaEventBus.publish(event))
                .isInstanceOf(EventPublicationFailedException.class)
                .hasMessageContaining("Failed to publish event: " + DummyEvent.class.getSimpleName())
                .hasCause(rootCause);

        verify(kafkaTemplate, never()).send(org.mockito.Mockito.anyString(), org.mockito.Mockito.any());
    }

    private static class DummyEvent implements IntegrationEvent {
    }
}
