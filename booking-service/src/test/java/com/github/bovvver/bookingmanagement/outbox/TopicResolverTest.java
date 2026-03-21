package com.github.bovvver.bookingmanagement.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TopicResolverTest {

    private final MessagingProperties messagingProperties = new MessagingProperties();
    private final TopicResolver topicResolver = new TopicResolver(messagingProperties);

    @Test
    void shouldResolveTopicWhenMappingExists() throws Exception {
        Map<String, String> topics = new HashMap<>();
        OutboxEvent event = createDummyOutboxEvent();
        topics.put(event.getEventType(), "test.topic");
        messagingProperties.getTopics().putAll(topics);

        String topic = topicResolver.resolve(event);

        assertThat(topic).isEqualTo("test.topic");
    }

    @Test
    void shouldThrowWhenTopicMappingDoesNotExist() throws Exception {
        messagingProperties.getTopics().clear();
        OutboxEvent event = createDummyOutboxEvent();

        assertThatThrownBy(() -> topicResolver.resolve(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No topic configured for event: " + event.getEventType());
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
