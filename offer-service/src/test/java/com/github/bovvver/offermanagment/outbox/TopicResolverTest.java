package com.github.bovvver.offermanagment.outbox;

import com.github.bovvver.contracts.IntegrationEvent;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TopicResolverTest {

    private final MessagingProperties messagingProperties = new MessagingProperties();
    private final TopicResolver topicResolver = new TopicResolver(messagingProperties);

    @Test
    void shouldResolveTopicWhenMappingExists() {
        Map<String, String> topics = new HashMap<>();
        topics.put(DummyEvent.class.getSimpleName(), "test.topic");
        messagingProperties.getTopics().putAll(topics);
        IntegrationEvent event = new DummyEvent();

        String topic = topicResolver.resolve(event);

        assertThat(topic).isEqualTo("test.topic");
    }

    @Test
    void shouldThrowWhenTopicMappingDoesNotExist() {
        messagingProperties.getTopics().clear();
        IntegrationEvent event = new DummyEvent();

        assertThatThrownBy(() -> topicResolver.resolve(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No topic configured for event: " + DummyEvent.class.getSimpleName());
    }

    private static class DummyEvent implements IntegrationEvent {
    }
}
