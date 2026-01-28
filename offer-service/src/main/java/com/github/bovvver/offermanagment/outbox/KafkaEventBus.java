package com.github.bovvver.offermanagment.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class KafkaEventBus implements EventBus {

    private final KafkaTemplate<String, JsonNode> kafka;
    private final TopicResolver topicResolver;

    /**
     * Publishes the given outbox event to the appropriate Kafka topic.
     *
     * @param outboxEvent The outbox event to be published. It contains the event type,
     *                    aggregate ID, and payload to be sent to the Kafka topic.
     */
    @Override
    public void publish(OutboxEvent outboxEvent) {
        String topic = topicResolver.resolve(outboxEvent);
        kafka.send(topic, outboxEvent.getAggregateId().toString(), outboxEvent.getPayload());
    }
}
