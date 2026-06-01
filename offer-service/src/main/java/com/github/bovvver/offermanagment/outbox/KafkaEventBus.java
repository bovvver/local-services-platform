package com.github.bovvver.offermanagment.outbox;

import com.github.bovvver.contracts.IntegrationEvent;
import com.github.bovvver.infrastructure.EventPublicationFailedException;
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
     * @param integrationEvent The outbox event to be published. It contains the event type,
     *                         aggregate ID, and payload to be sent to the Kafka topic.
     */
    @Override
    public void publish(IntegrationEvent integrationEvent) {
        try {
            String topic = topicResolver.resolve(integrationEvent);
            kafka.send(topic, integrationEvent);
        } catch (Exception e) {
            throw new EventPublicationFailedException(integrationEvent.getClass().getSimpleName(), e);
        }
    }
}
