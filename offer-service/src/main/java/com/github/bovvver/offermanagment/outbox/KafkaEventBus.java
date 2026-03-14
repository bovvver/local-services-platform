package com.github.bovvver.offermanagment.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.contracts.IntegrationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class KafkaEventBus implements EventBus {

    private final KafkaTemplate<String, String> kafka;
    private final TopicResolver topicResolver;
    private final ObjectMapper objectMapper;

    /**
     * Publishes the given integration event to the appropriate Kafka topic.
     * The event is serialized to a raw JSON string, avoiding Jackson type header issues.
     *
     * @param integrationEvent The event to be published.
     */
    @Override
    public void publish(IntegrationEvent integrationEvent) {
        try {
            String topic = topicResolver.resolve(integrationEvent);
            String payload = objectMapper.writeValueAsString(integrationEvent);
            kafka.send(topic, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event: " + integrationEvent.getClass().getSimpleName(), e);
        }
    }
}
