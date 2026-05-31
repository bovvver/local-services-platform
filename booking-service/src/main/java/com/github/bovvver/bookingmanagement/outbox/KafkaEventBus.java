package com.github.bovvver.bookingmanagement.outbox;

import com.github.bovvver.bookingmanagement.infrastructure.EventPublicationFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class KafkaEventBus implements EventBus {

    private final KafkaTemplate<String, Object> kafka;
    private final TopicResolver topicResolver;

    /**
     * Publishes the given outbox event to the appropriate Kafka topic.
     * The payload is sent as a JSON.
     *
     * @param outboxEvent The outbox event to be published. It contains the event type,
     *                    aggregate ID, and payload to be sent to the Kafka topic.
     */
    @Override
    public void publish(OutboxEvent outboxEvent) {
        try {
            String topic = topicResolver.resolve(outboxEvent);
            Object payload = outboxEvent.getPayload();
            kafka.send(topic, outboxEvent.getAggregateId().toString(), payload);
        } catch (Exception e) {
            throw new EventPublicationFailedException(outboxEvent.getEventType(), e);
        }
    }
}
