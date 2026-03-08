package com.github.bovvver.bookingmanagement.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class KafkaEventBus implements EventBus {

    private final KafkaTemplate<String, String> kafka;
    private final TopicResolver topicResolver;

    /**
     * Publishes the given outbox event to the appropriate Kafka topic.
     * The payload is sent as a raw JSON string, avoiding Jackson type header issues.
     *
     * @param outboxEvent The outbox event to be published. It contains the event type,
     *                    aggregate ID, and payload to be sent to the Kafka topic.
     */
    @Override
    public void publish(OutboxEvent outboxEvent) {
        try {
            String topic = topicResolver.resolve(outboxEvent);
            kafka.send(topic, outboxEvent.getAggregateId().toString(), outboxEvent.getPayload().toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish event: " + outboxEvent.getEventType(), e);
        }
    }
}
